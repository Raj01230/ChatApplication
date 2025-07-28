package com.dollop.app.repo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.dollop.app.entity.RoomMessage;
import com.dollop.app.enums.MessageStatus;

public interface IRoomMessageRepo extends JpaRepository<RoomMessage, String> {
	List<RoomMessage> findByRoom_IdAndIsDeletedFalse(String id, Sort sort);

	@Transactional
	@Modifying
	@Query(value = """
				UPDATE room_message rm
				JOIN user s ON s.id = rm.sender_id
				SET rm.status_type = 'DELIVERED'
				WHERE rm.room_id IN (
					SELECT rm2.room_id
					FROM room_member rm2
					JOIN user u ON u.id = rm2.user_id
					WHERE u.email = :email
				)
				AND rm.status_type = 'SENT'
				AND s.email <> :email
			""", nativeQuery = true)
	Integer markAllMessagesAsDeliveredForUser(@Param("email") String email);

	@Query("""
			  SELECT m FROM RoomMessage m
			  WHERE m.room.id IN (
			    SELECT rm.chatRoom.id FROM RoomMember rm WHERE rm.user.email = :email
			  )
			  AND m.statusType = 'DELIVERED'
			  AND m.sender.email <> :email
			""")
	List<RoomMessage> getMessagesAsDeliveredForUser(@Param("email") String email);

	@Transactional
	@Modifying
	@Query(value = """
			UPDATE room_message rm
			JOIN user u ON u.id = rm.sender_id
			SET rm.status_type = 'SEEN'
			WHERE rm.room_id = :roomId
			AND rm.status_type = 'DELIVERED'
			AND u.email <> :email
			""", nativeQuery = true)
	Integer markAllMessagesAsSeenForUser(@Param("email") String email, String roomId);

	@Query("""
			    SELECT m FROM RoomMessage m
			    WHERE m.room.id = :roomId
			    AND m.sender.email <> :senderId
			    AND m.statusType = :status
			""")
	List<RoomMessage> findByRoomIdAndSenderIdsAndStatus(@Param("roomId") String roomId,
			@Param("senderId") String senderId, @Param("status") MessageStatus status);
}
