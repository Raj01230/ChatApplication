package com.dollop.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dollop.app.entity.RoomMember;
import com.dollop.app.exception.ResourceNotFoundException;
import com.dollop.app.repo.IRoomMemberRepo;
import com.dollop.app.response.RoomMemberResponse;
import com.dollop.app.service.IRoomMemberService;
import com.dollop.app.service.IUserService;
import com.dollop.app.utils.ErrorConstant;

@Service
public class RoomMemberServiceImpl implements IRoomMemberService {
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoomMemberRepo roomMemberRepo;

	@Override
	public RoomMemberResponse roomMemberToRoomMemberResponse(RoomMember member) {
		return RoomMemberResponse.builder().id(member.getId()).isAdmin(member.getIsAdmin())
				.isAvailable(member.getIsAvailable()).user(userService.userToUserResponse(member.getUser())).build();
	}

	@Override
	public List<RoomMemberResponse> roomMemberToRoomMemberResponse(List<RoomMember> member) {
		return member.stream().map(this::roomMemberToRoomMemberResponse).toList();
	}

	@Override
	public RoomMember findById(String id) {
		return roomMemberRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorConstant.ROOM_MEMBER_NOT_FOUND));
	}

	@Override
	public boolean isUserMemberOfChatRoom(String roomId, String userId) {

		return roomMemberRepo.existsByChatRoom_IdAndUser_Id(roomId, userId);
	}

	@Override
	public RoomMember save(RoomMember roomMember) {
		return roomMemberRepo.save(roomMember);
	}

	@Override
	public RoomMember findUserMemberOfChatRoom(String roomId, String userId) {

		return roomMemberRepo.findByChatRoom_IdAndUser_Id(roomId, userId);
	}

}
