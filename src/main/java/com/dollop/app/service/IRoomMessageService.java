package com.dollop.app.service;

import java.util.List;

import com.corundumstudio.socketio.SocketIOClient;
import com.dollop.app.entity.RoomMessage;
import com.dollop.app.enums.MessageStatus;
import com.dollop.app.enums.MessageType;
import com.dollop.app.request.RoomMessageRequest;
import com.dollop.app.response.ApiResponse;
import com.dollop.app.response.RoomMessageResponse;

//@CrossOrigin(origins = "http://localhost:4200/", allowCredentials = "true")
public interface IRoomMessageService {

	ApiResponse updateMessage(String messageId, MessageType type);

	List<RoomMessageResponse> messageToMessageResponse(List<RoomMessage> message);

	RoomMessageResponse messageToMessageResponse(RoomMessage message);

	RoomMessageResponse sendMessage(SocketIOClient senderClient, RoomMessageRequest request);

	RoomMessageResponse changeStatus(String id, MessageStatus delivered);

	void deliveredMessageStatus(String email);

	List<RoomMessageResponse> getMessagesAsDeliveredForUser(String email);

	void changeStatus(String id, String email, MessageStatus seen);

	List<RoomMessageResponse> getMessagesAsSeenForUser(String id, String email, MessageStatus messageType);

	ApiResponse deleteMessageForMe(List<String> messageIds);

	ApiResponse deleteMessageForEveryone(List<String> messageIds);

	ApiResponse deleteMessageForLeavedMember(String email, String msgId);

	ApiResponse getMessagesByRoomId(String chatRoomId, Integer pageNo, Integer pageSize);
}
