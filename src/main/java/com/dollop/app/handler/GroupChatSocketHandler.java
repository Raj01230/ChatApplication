package com.dollop.app.handler;

import org.springframework.stereotype.Component;

@Component
public class GroupChatSocketHandler {

//	private final SocketIOServer server;
//	private final IGroupChatRepo chatMessageRepository;
//	private IGroupService groupService;
//	private IUserService userService;
//	private AppUtils utils;
//
//	public GroupChatSocketHandler(SocketIOServer server, IGroupChatRepo chatMessageRepository,
//			IGroupService groupService, IUserService userService, AppUtils utils) {
//		super();
//		this.server = server;
//		this.chatMessageRepository = chatMessageRepository;
//		this.groupService = groupService;
//		this.userService = userService;
//		this.utils = utils;
//	}
//
//	@OnConnect
//	public void onConnect(SocketIOClient client) {
//		System.out.println("✅ Client connected: " + client.getSessionId());
//	}
//
//	@OnDisconnect
//	public void onDisconnect(SocketIOClient client) {
//		System.out.println("❌ Client disconnected: " + client.getSessionId());
//	}
//
//	@OnEvent("chatMessage")
//	public void onChatMessage(SocketIOClient client, GroupChatMessageRequest chatMessage) {
//		System.out.println("📩 Message received: " + chatMessage);
//		GroupChat chat = GroupChat.builder().group(groupService.findById(chatMessage.getGroupId()))
//				.sender(userService.findByUserId(utils.getIdByToken())).text(chatMessage.getMsg()).build();
//		chatMessageRepository.save(chat);
//		// Broadcast to group
//		server.getRoomOperations(chatMessage.getGroupId()).sendEvent("chatMessage", chatMessage);
//	}
//
//	@OnEvent("join")
//	public void onJoin(SocketIOClient client, String groupId) {
//		client.joinRoom(groupId);
//		System.out.println("👥 Client joined group: " + groupId);
//	}
}
