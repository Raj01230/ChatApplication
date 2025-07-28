package com.dollop.app.request;

import java.util.List;

import com.dollop.app.annotation.Trimmed;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class ChatRoomRequest {
	private Boolean isGroup;

	private String roomName; // For group chat

	private List<@NotBlank(message = "The given member id must not be null") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format") String> memberIds;
}
