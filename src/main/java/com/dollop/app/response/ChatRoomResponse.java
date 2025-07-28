package com.dollop.app.response;

import java.util.List;

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
@JsonInclude(Include.NON_NULL)
public class ChatRoomResponse {
	private String id;

	private String name; // null for private chat

	private Boolean isGroup; // true = group chat, false = private chat

	private UserResponse createdBy;

	private List<RoomMemberResponse> members;
}
