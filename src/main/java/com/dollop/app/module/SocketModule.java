package com.dollop.app.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.dollop.app.enums.MessageStatus;
import com.dollop.app.request.DeleteForEveryone;
import com.dollop.app.request.DeliveredRequest;
import com.dollop.app.request.JoinRoomRequest;
import com.dollop.app.request.LeaveRoomRequest;
import com.dollop.app.request.RoomMessageRequest;
import com.dollop.app.request.TypingRequest;
import com.dollop.app.response.ChatRoomResponse;
import com.dollop.app.response.RoomMessageResponse;
import com.dollop.app.response.UserResponse;
import com.dollop.app.service.IChatRoomService;
import com.dollop.app.service.IRoomMessageService;
import com.dollop.app.service.IUserService;

import jakarta.annotation.PreDestroy;

@Component
public class SocketModule {

	private static final Logger log = LoggerFactory.getLogger(SocketModule.class);

	private final SocketIOServer server;
	private final IRoomMessageService socketService;
	@Autowired
	@Lazy
	private IRoomMessageService messageService;

	@Autowired
	@Lazy
	private IChatRoomService chatRoomService;
	@Autowired
	@Lazy
	private IUserService userService;

	// For storing connected users and their clients
	private final Map<String, SocketIOClient> clients = new HashMap<>();
	private final Set<String> onlineUsers = new HashSet<>();

	public SocketModule(SocketIOServer server, IRoomMessageService socketService) {
		this.server = server;
		this.socketService = socketService;

		server.addConnectListener(onConnected());
		server.addDisconnectListener(onDisconnected());
		server.addEventListener("send_message", RoomMessageRequest.class, onChatReceived());
		server.addEventListener("join_room", JoinRoomRequest.class, onJoinRoom());
		server.addEventListener("message_delivered", DeliveredRequest.class, deliverdMessages());
		server.addEventListener("message_seen", DeliveredRequest.class, seenMessages());
		server.addEventListener("leave_room", LeaveRoomRequest.class, onLeaveRoom());
		server.addEventListener("delete_for_everyone", DeleteForEveryone.class, deleteForEveryone());
		server.addEventListener("remove_member", DeleteForEveryone.class, removeMembers());
		server.addEventListener("typingEvent", TypingRequest.class, onTypingEvent());

	}

	private DataListener<TypingRequest> onTypingEvent() {
		return (client, data, ackSender) -> {
			data.getRoom().getMembers().stream().filter(m -> !m.getUser().getId().equals(data.getUser().getId())) // not
																													// sender
					.filter(m -> (onlineUsers.contains(m.getUser().getEmail()) && m.getIsAvailable())) // only if online
					.forEach(m -> {
						server.getRoomOperations(m.getUser().getEmail()).getClients()
								.forEach(c -> c.sendEvent("typing_event", data));
					});
		};
	}

	public void addMemberEvent(ChatRoomResponse chatRoom) {
		server.getRoomOperations(chatRoom.getId()).getClients().forEach(client1 -> {
			client1.sendEvent("add_members", chatRoom);
		});
		List<String> emails = chatRoom.getMembers().stream().map(m -> m.getUser().getEmail()).toList();
		emails.forEach(e -> {
			server.getRoomOperations(e).getClients().forEach(c -> {
				c.sendEvent("add_members", chatRoom);
				System.err.println(c.getAllRooms());
			});
		});
	}

	public void createRoom(ChatRoomResponse chatRoom) {
		List<String> emails = chatRoom.getMembers().stream().map(m -> m.getUser().getEmail()).toList();
		System.err.println("========================" + emails);
		emails.forEach(e -> {
			server.getRoomOperations(e).getClients().forEach(c -> {
				System.err.println("========================" + c);
				c.sendEvent("createRoom", chatRoom);
				System.err.println(c.getAllRooms() + " =]]]=========]  " + e);
			});
		});

	}

	private DataListener<DeleteForEveryone> removeMembers() {
		return (client, data, ackSender) -> {
			Map map = chatRoomService.removeRoomMember(data.getIds(), data.getRoomId());
			List<String> removedEmails = (List<String>) map.get("emails");

			ChatRoomResponse roomResponse = (ChatRoomResponse) map.get("chatRoom");
			System.err.println("email================>>>>>>. " + removedEmails);
			server.getRoomOperations(data.getRoomId()).getClients().forEach(client1 -> {
				client1.sendEvent("remove_members", roomResponse);
				String email = client1.get("email");
				System.err.println("email================>>>>>>.111111111 " + client1.getAllRooms());
				// If client matches removed member
				if (email != null && removedEmails.contains(email)) {
					System.err.println("email================>>>>>>.22222222 " + email);
					client1.leaveRoom(data.getRoomId()); // 👈 force leave
				}
			});
		};
	}

	private DataListener<DeleteForEveryone> deleteForEveryone() {
		return (client, data, ackSender) -> {
			List<String> ids = data.getIds();
			String roomId = data.getRoomId();
			messageService.deleteMessageForEveryone(ids);
			server.getRoomOperations(roomId).getClients().forEach(c -> {
				c.sendEvent("deleted_message_for_everyone", ids); // You can also just send `ids` if you want
			});
			log.info("🚪 Client {} left room {}", client.getSessionId(), ids);
		};

	}

	private DataListener<LeaveRoomRequest> onLeaveRoom() {
		return (client, data, ackSender) -> {
			String roomId = data.getRoomId();
			client.leaveRoom(roomId);
			log.info("🚪 Client {} left room {}", client.getSessionId(), roomId);
		};

	}

	private DataListener<DeliveredRequest> seenMessages() {
		return (client, data, ackSender) -> {

			List<RoomMessageResponse> list = messageService.getMessagesAsSeenForUser(data.getId(), data.getEmail(),
					MessageStatus.DELIVERED);

			messageService.changeStatus(data.getId(), data.getEmail(), MessageStatus.SEEN);

			for (RoomMessageResponse msg : list) {
				String senderEmail = msg.getSender() != null ? msg.getSender().getEmail() : null;
				msg.setStatus(MessageStatus.SEEN);
				if (senderEmail != null) {
					for (SocketIOClient connectedClient : server.getAllClients()) {
						String connectedEmail = connectedClient.get("email");
						if (connectedEmail != null && connectedEmail.equals(senderEmail)) {

							connectedClient.sendEvent("seen_status", list);
						}
					}
				}
			}
		};
	}

	private void seenMessages(String id, String email, SocketIOClient client) {

		List<RoomMessageResponse> list = messageService.getMessagesAsSeenForUser(id, email, MessageStatus.DELIVERED);
		messageService.changeStatus(id, email, MessageStatus.SEEN);

		for (RoomMessageResponse msg : list) {
			String senderEmail = msg.getSender() != null ? msg.getSender().getEmail() : null;
			msg.setStatus(MessageStatus.SEEN);
			if (senderEmail != null) {
				for (SocketIOClient connectedClient : server.getAllClients()) {
					String connectedEmail = connectedClient.get("email");
					if (connectedEmail != null && connectedEmail.equals(senderEmail)) {

						connectedClient.sendEvent("seen_status", list);
					}
				}
			}
		}
	};

	private DataListener<DeliveredRequest> deliverdMessages() {
		return (client, data, ackSender) -> {
			if (onlineUsers.contains(data.getEmail())) {

				messageService.changeStatus(data.getId(), MessageStatus.DELIVERED);
			}
		};
	}

	private void deliverdMessages(String email, SocketIOClient client) {
		// 1. Update DB
		messageService.deliveredMessageStatus(email);

		// 2. Get messages just marked as delivered
		List<RoomMessageResponse> list = messageService.getMessagesAsDeliveredForUser(email);

		// 3. Send to RECEIVER (i.e., this client)
		client.sendEvent("changed_status", list);

		// 4. Notify SENDERS about delivery

		for (RoomMessageResponse msg : list) {
			String senderEmail = msg.getSender() != null ? msg.getSender().getEmail() : null;

			if (senderEmail != null) {
				for (SocketIOClient connectedClient : server.getAllClients()) {
					String connectedEmail = connectedClient.get("email");

					if (connectedEmail != null && connectedEmail.equals(senderEmail)) {

						connectedClient.sendEvent("changed_status", list);
					}
				}
			}
		}
	}

	private ConnectListener onConnected() {
		return client -> {

			String email = client.getHandshakeData().getSingleUrlParam("email");

			if (email != null && !email.isEmpty()) {
				client.joinRoom(email);
				client.set("email", email);
				onlineUsers.add(email);
				log.info("User joined with email: {}", email);
				deliverdMessages(email, client);

				broadcastOnlineUsers();
			}

			log.info("Socket ID[{}] connected.", client.getSessionId());
		};
	}

	private DisconnectListener onDisconnected() {
		return client -> {
			log.info("Client [{}] - Disconnected", client.getSessionId());

			String email = client.getHandshakeData().getSingleUrlParam("email");
			if (email != null) {
				onlineUsers.remove(email);
				UserResponse userResponse = userService.setLastSeen(email);
				sendlastSeenEvent(userResponse);
				log.info("User [{}] removed from online list", email);
				broadcastOnlineUsers();
			}

			// Clean up client map
			clients.values().removeIf(c -> c.getSessionId().equals(client.getSessionId()));
		};
	}

//	private DataListener<RoomMessageRequest> onChatReceived() {
//		return (senderClient, data, ackSender) -> {
//			RoomMessageResponse response = socketService.sendMessage(senderClient, data);
//
//			server.getRoomOperations(data.getRoomId()).getClients().forEach(client -> {
//				client.sendEvent("get_message", response);
//			});
//
//			log.info("Message sent to room [{}]: {}", data.getRoomId(), data.getContent());
//		};
//	}

	private void sendlastSeenEvent(UserResponse userResponse) {
		onlineUsers.forEach(e -> server.getRoomOperations(e).getClients().forEach(c -> {
			c.sendEvent("lastSeen", userResponse);
			System.err.println("lastSeen ----->1111 " + e);
		}));
		System.err.println("lastSeen -----> " + userResponse);

	}

	private DataListener<RoomMessageRequest> onChatReceived() {
		return (senderClient, data, ackSender) -> {
			RoomMessageResponse response = socketService.sendMessage(senderClient, data);

			// Send message to all in room
			if (response.getChatRoom().getIsGroup()) {
				// 🔐 Send only to allowed emails if group chat
				List<String> allowedEmails = response.getChatRoom().getMembers().stream()
						.filter(m -> m.getIsAvailable()).map(m -> m.getUser().getEmail()).collect(Collectors.toList());

				server.getRoomOperations(data.getRoomId()).getClients().forEach(client -> {
					String email = client.get("email");
					System.err.println("==================> eamil --- " + email);
					if (email != null && allowedEmails.contains(email)) {
						System.err.println("==================> eamil 1 --- " + email);
						client.sendEvent("get_message", response);
					} else {
						messageService.deleteMessageForLeavedMember(email, response.getId());
						log.warn("Skipped unauthorized user [{}] for group [{}]", email, data.getRoomId());
					}
				});
			} else {
				// 🔁 Direct chat — send to all in room
				server.getRoomOperations(data.getRoomId()).getClients().forEach(client -> {
					client.sendEvent("get_message", response);
				});
			}

			// ✅ Mark as SEEN if receiver is also in same room
			String receiverEmail = response.getChatRoom().getMembers().stream().map(m -> m.getUser().getEmail())
					.filter(email -> !email.equals(response.getSender().getEmail())).findFirst().orElse(null);

			if (receiverEmail != null) {
				boolean receiverOnline = onlineUsers.contains(receiverEmail);

				for (SocketIOClient c : server.getRoomOperations(data.getRoomId()).getClients()) {
					String connectedEmail = c.get("email");

					if (connectedEmail != null && connectedEmail.equals(receiverEmail)) {
						log.info("✅ Both sender and receiver are in room, marking as SEEN.");
						messageService.changeStatus(response.getId(), receiverEmail, MessageStatus.SEEN);

						// Inform both about SEEN status
						server.getRoomOperations(data.getRoomId()).getClients().forEach(client -> {
							response.setStatus(MessageStatus.SEEN);
							client.sendEvent("seen_status", List.of(response));
						});
						break;
					}
				}
			}

			log.info("Message sent to room [{}]: {}", data.getRoomId(), data.getContent());
		};
	}

	private DataListener<JoinRoomRequest> onJoinRoom() {
		return (client, data, ackSender) -> {
			if (data.getRoomId() != null && !data.getRoomId().isEmpty()) {
				client.joinRoom(data.getRoomId());
				clients.put(data.getRoomId(), client);
//				seenMessages(data.getRoomId(), data.getEmail(), client);
				log.info("Client [{}] joined room: {}", client.getSessionId(), data.getRoomId());

			} else {
				log.warn("join_room event received without valid roomId.");
			}
		};
	}

	private void broadcastOnlineUsers() {
		for (SocketIOClient client : server.getAllClients()) {
			client.sendEvent("onlineUser", onlineUsers);
		}
		log.info("Broadcasted online users: {}", onlineUsers);
	}

	@PreDestroy
	public void stopSocketServer() {
		if (server != null) {
			server.stop();
			log.info("Socket server stopped.");
		}
	}
}
