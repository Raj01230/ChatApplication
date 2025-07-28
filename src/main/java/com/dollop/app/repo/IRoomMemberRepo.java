package com.dollop.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dollop.app.entity.RoomMember;

public interface IRoomMemberRepo extends JpaRepository<RoomMember, String> {

	Boolean existsByChatRoom_IdAndUser_Id(String roomId, String userId);

	RoomMember findByChatRoom_IdAndUser_Id(String roomId, String userId);

}
