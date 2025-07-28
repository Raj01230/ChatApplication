package com.dollop.app.request;

import com.dollop.app.annotation.Trimmed;
import com.dollop.app.response.ChatRoomResponse;
import com.dollop.app.response.UserResponse;
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
public class TypingRequest {
	private UserResponse user;
	private ChatRoomResponse room;
}
