package com.dollop.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dollop.app.response.ApiResponse;
import com.dollop.app.service.IRoomMessageService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/room-message")
@Validated
@CrossOrigin(origins = "http://localhost:4200/", allowCredentials = "true")
public class RoomMessageController {

	@Autowired
	private IRoomMessageService messageService;

//	@PostMapping("/send-message")
//	public ResponseEntity<ApiResponse> sendMessage(@Valid @RequestBody RoomMessageRequest messageRequest) {
//		System.err.println("send Msg ===========> " + messageRequest);
//		return new ResponseEntity<ApiResponse>(messageService.sendMessage(messageRequest), HttpStatus.CREATED);
//	}

	@GetMapping("/get-messages-by-id")
	public ResponseEntity<ApiResponse> getMessagesByRoomId(
			@NotBlank(message = "The given id must not be null") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format") @RequestParam String id,
			@RequestParam(required = false, defaultValue = "0") Integer pageNo,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize) {
		return new ResponseEntity<ApiResponse>(messageService.getMessagesByRoomId(id, pageNo, pageSize), HttpStatus.OK);
	}

	@DeleteMapping("/delete-messages-for-me")
	public ResponseEntity<ApiResponse> getMessagesByRoomId(
			@RequestBody List<@NotBlank(message = "The given id must not be null") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format") String> ids) {
		return new ResponseEntity<ApiResponse>(messageService.deleteMessageForMe(ids), HttpStatus.OK);
	}
}
