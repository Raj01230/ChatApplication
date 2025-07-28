package com.dollop.app.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dollop.app.entity.DeletedMessage;
import com.dollop.app.entity.RoomMessage;

public interface IDeletedMessageRepo extends JpaRepository<DeletedMessage, String> {
	@Query("""
			  SELECT m FROM RoomMessage m
			  WHERE   m.isDeleted = false AND m.room.id = :roomId AND (
			    NOT EXISTS (
			      SELECT mv FROM DeletedMessage mv
			      WHERE mv.message = m AND mv.user.id = :userId AND mv.visible = false
			    )
			  )
			""")
	Page<RoomMessage> findVisibleMessagesForUser(@Param("roomId") String roomId, @Param("userId") String userId,
			Pageable pageable);

	@Query("""
			SELECT CASE
			  WHEN EXISTS (
			    SELECT d FROM DeletedMessage d
			    WHERE d.message.id = :msgId AND d.user.id = :userId AND d.visible = false
			  )
			  OR EXISTS (
			    SELECT m FROM RoomMessage m
			    WHERE m.id = :msgId AND m.isDeleted = true
			  )
			  THEN true ELSE false
			END
			""")
	boolean isMessageHiddenOrDeleted(@Param("msgId") String msgId, @Param("userId") String userId);

}
