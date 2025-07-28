package com.dollop.app.service;

import java.util.List;

import com.dollop.app.entity.RoomMember;
import com.dollop.app.response.RoomMemberResponse;

public interface IRoomMemberService {
	RoomMemberResponse roomMemberToRoomMemberResponse(RoomMember member);

	List<RoomMemberResponse> roomMemberToRoomMemberResponse(List<RoomMember> member);

	RoomMember findById(String id);

	boolean isUserMemberOfChatRoom(String id, String userId);

	RoomMember findUserMemberOfChatRoom(String roomId, String userId);

	RoomMember save(RoomMember roomMember);
}
