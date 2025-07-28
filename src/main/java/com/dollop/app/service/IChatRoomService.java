package com.dollop.app.service;

import java.util.List;
import java.util.Map;

import com.dollop.app.entity.ChatRoom;
import com.dollop.app.request.ChatRoomRequest;
import com.dollop.app.response.ApiResponse;
import com.dollop.app.response.ChatRoomResponse;

public interface IChatRoomService {
	ApiResponse createRoom(ChatRoomRequest roomRequest);

	ApiResponse getRoomById(String id);

	ChatRoom findRoomById(String id);

	ApiResponse getAllRooms();

	ApiResponse updateRoomName(String id, String name);

	ApiResponse getRoomsForUser(String userId);

	ChatRoomResponse chatRoomtoChatRoomResponse(ChatRoom chatRoom);

	List<ChatRoomResponse> chatRoomtoChatRoomResponse(List<ChatRoom> chatRoom);

	ApiResponse addRoomMember(String id, List<String> userId);

	Map removeRoomMember(List<String> ids, String roomId);
}
