package com.dollop.app.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dollop.app.entity.ChatRoom;

public interface IChatRoomRepo extends JpaRepository<ChatRoom, String> {
	@Query("""
			SELECT cr
			FROM ChatRoom cr
			JOIN cr.member p1
			JOIN cr.member p2
			WHERE cr.isGroup = false
			AND p1.user.id = :userId1
			AND p2.user.id = :userId2
			""")
	Optional<ChatRoom> findOneToOneChatRoomBetween(@Param("userId1") String userId1, @Param("userId2") String userId2);

	@Query("""
			    SELECT c FROM ChatRoom c
			    JOIN c.member m
			    LEFT JOIN RoomMessage msg ON msg.room = c
			    WHERE m.user.id = :userId
			    GROUP BY c.id
			    ORDER BY MAX(msg.createdAt) DESC
			""")
	List<ChatRoom> findUserChatRoomsSortedByLastMessage(@Param("userId") String userId);

}
