package com.dollop.app.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.corundumstudio.socketio.SocketIOClient;
import com.dollop.app.entity.ChatRoom;
import com.dollop.app.entity.DeletedMessage;
import com.dollop.app.entity.RoomMessage;
import com.dollop.app.entity.User;
import com.dollop.app.enums.MessageStatus;
import com.dollop.app.enums.MessageType;
import com.dollop.app.exception.ResourceNotFoundException;
import com.dollop.app.repo.IDeletedMessageRepo;
import com.dollop.app.repo.IRoomMessageRepo;
import com.dollop.app.request.RoomMessageRequest;
import com.dollop.app.response.ApiResponse;
import com.dollop.app.response.RoomMessageResponse;
import com.dollop.app.service.IChatRoomService;
import com.dollop.app.service.IPageService;
import com.dollop.app.service.IRoomMessageService;
import com.dollop.app.service.IUserService;
import com.dollop.app.utils.AppUtils;
import com.dollop.app.utils.ErrorConstant;
import com.dollop.app.utils.JwtUtil;

@Service
//@CrossOrigin(origins = "http://localhost:4200/", allowCredentials = "true")
public class RoomMessageServiceImpl implements IRoomMessageService {
	@Autowired
	private IUserService userService;
	@Autowired
	private IChatRoomService chatRoomService;
	@Autowired
	private IRoomMessageRepo roomMessageRepo;
	@Autowired
	private IDeletedMessageRepo deletedMessageRepo;
	@Autowired
	private IPageService pageService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private AppUtils appUtils;

	@Override
	public RoomMessageResponse sendMessage(SocketIOClient senderClient, RoomMessageRequest request) {
		User user = userService
				.findByEmail(jwtUtil.getUsername(senderClient.getHandshakeData().getSingleUrlParam("token")));
		ChatRoom chatRoom = chatRoomService.findRoomById(request.getRoomId());
		RoomMessage roomMessage = RoomMessage.builder().content(request.getContent()).sender(user).room(chatRoom)
				.type(MessageType.TEXT).build();
		if (request.getReplyToMessageId() != null && !request.getReplyToMessageId().isEmpty())
			roomMessage.setReplayMessage(findById(request.getReplyToMessageId()));
		roomMessageRepo.save(roomMessage);
		return messageToMessageResponse(roomMessage);
	}

	@Override
	public ApiResponse getMessagesByRoomId(String chatRoomId, Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
		chatRoomService.findRoomById(chatRoomId);
		Page<RoomMessage> messages = deletedMessageRepo.findVisibleMessagesForUser(chatRoomId, appUtils.getIdByToken(),
				pageable);
		messages.getContent().forEach(msg -> {
			RoomMessage reply = msg.getReplayMessage();
			if (reply != null && deletedMessageRepo.isMessageHiddenOrDeleted(reply.getId(), appUtils.getIdByToken())) {
				msg.setReplayMessage(null); // Remove invisible reply
			}
		});
		messages.getContent().forEach((m) -> System.err.println("------->> " + m.getCreatedAt()));
		if (messages == null || messages.isEmpty()) {
			return ApiResponse.builder().message("Room have no messages!").response(null).build();
		}
		return ApiResponse.builder().message("Messages of this room!").response(
				Map.of("page", pageService.getAllData(messageToMessageResponse(messages.getContent()), messages)))
				.build();
	}

	@Override
	public RoomMessageResponse messageToMessageResponse(RoomMessage message) {
		RoomMessage replyMessage = message.getReplayMessage(); // 👈 Spelling fix from 'replay' to 'reply'

		RoomMessageResponse replyMessageResponse = null;
		if (replyMessage != null) {
			replyMessageResponse = RoomMessageResponse.builder().id(replyMessage.getId())
					.content(replyMessage.getContent()).sender(userService.userToUserResponse(replyMessage.getSender()))
					.type(replyMessage.getType()).time(Timestamp.valueOf(replyMessage.getCreatedAt().toLocalDateTime()))
					.build();
		}

		return RoomMessageResponse.builder().id(message.getId())
				.chatRoom(chatRoomService.chatRoomtoChatRoomResponse(message.getRoom()))
				.replyMessage(replyMessageResponse) // 👈 One-level reply
				.sender(userService.userToUserResponse(message.getSender())).content(message.getContent())
				.status(message.getStatusType()).time(Timestamp.valueOf(message.getCreatedAt().toLocalDateTime()))
				.type(message.getType()).build();
	}

	@Override
	public List<RoomMessageResponse> messageToMessageResponse(List<RoomMessage> message) {

		return message.stream().map(this::messageToMessageResponse).toList();
	}

	public RoomMessage findById(String id) {
		return roomMessageRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorConstant.ROOM_MESSAGE_NOT_FOUND));
	}

	@Override
	public ApiResponse deleteMessageForLeavedMember(String email, String msgId) {
		User user = userService.findByEmail(email);

		RoomMessage message = findById(msgId);
//			message.setIsDeleted(true);
//			roomMessageRepo.save(message);
		DeletedMessage deletedMessage = DeletedMessage.builder().message(message).visible(false).user(user).build();
		System.err.println(deletedMessage);
		deletedMessageRepo.save(deletedMessage);

		return ApiResponse.builder().message("Messages Deleted Successfully!").build();
	}

	@Override
	public ApiResponse deleteMessageForMe(List<String> messageIds) {
		User user = userService.findById(appUtils.getIdByToken());
		messageIds.stream().forEach(id -> {
			RoomMessage message = findById(id);
			DeletedMessage deletedMessage = DeletedMessage.builder().message(message).visible(false).user(user).build();
			System.err.println(deletedMessage);
			deletedMessageRepo.save(deletedMessage);
		});
		return ApiResponse.builder().message("Messages Deleted Successfully!").build();
	}

	@Override
	public ApiResponse deleteMessageForEveryone(List<String> messageIds) {
		messageIds.stream().forEach(id -> {
			RoomMessage message = findById(id);
			message.setIsDeleted(true);
			roomMessageRepo.save(message);
		});
		return ApiResponse.builder().message("Messages Deleted Successfully!").build();
	}

	@Override
	public ApiResponse updateMessage(String messageId, MessageType type) {
		return null;
	}

	@Override
	public RoomMessageResponse changeStatus(String id, MessageStatus delivered) {

		RoomMessage roomMessage = findById(id);
		roomMessage.setStatusType(delivered);
		roomMessageRepo.save(roomMessage);
		return messageToMessageResponse(roomMessage);
	}

	@Override
	public void deliveredMessageStatus(String email) {
		System.err.println(roomMessageRepo.markAllMessagesAsDeliveredForUser(email));
	}

	@Override
	public void changeStatus(String id, String email, MessageStatus seen) {
		roomMessageRepo.markAllMessagesAsSeenForUser(email, id);
	}

	@Override
	public List<RoomMessageResponse> getMessagesAsDeliveredForUser(String email) {
		return messageToMessageResponse(roomMessageRepo.getMessagesAsDeliveredForUser(email));
	}

	@Override
	public List<RoomMessageResponse> getMessagesAsSeenForUser(String id, String email, MessageStatus messageType) {
		return messageToMessageResponse(roomMessageRepo.findByRoomIdAndSenderIdsAndStatus(id, email, messageType));
	}

}
