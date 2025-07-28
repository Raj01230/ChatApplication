package com.dollop.app.response;

import java.sql.Timestamp;

import com.dollop.app.enums.UserStatusType;
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
public class UserResponse {

	private String id;

	private String name;

	private String username;

	private String email;

	private String profilePicUrl;

	private UserStatusType statusType; // e.g., "Available", "Busy"

	private Timestamp lastSeen;
}
