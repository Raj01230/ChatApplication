package com.dollop.app.response;

import java.sql.Timestamp;

import com.dollop.app.annotation.Trimmed;
import com.dollop.app.enums.MessageStatus;
import com.dollop.app.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Trimmed
@JsonInclude(Include.NON_NULL)
public class RoomMessageResponse {

	private String id;

	private ChatRoomResponse chatRoom;

	private UserResponse sender;

	private String content;

	private MessageStatus status;

	private MessageType type;

	private RoomMessageResponse replyMessage;

	private Timestamp time;
}
