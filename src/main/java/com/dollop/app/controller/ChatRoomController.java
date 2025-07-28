package com.dollop.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dollop.app.request.ChatRoomRequest;
import com.dollop.app.response.ApiResponse;
import com.dollop.app.service.IChatRoomService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/chat-room")
@Validated
@CrossOrigin(origins = "http://localhost:4200/", allowCredentials = "true")
public class ChatRoomController {
	@Autowired
	private IChatRoomService chatRoomService;

	@PostMapping("/create-room")
	public ResponseEntity<ApiResponse> createRoom(@Valid @RequestBody ChatRoomRequest roomRequest) {
		return new ResponseEntity<ApiResponse>(chatRoomService.createRoom(roomRequest), HttpStatus.CREATED);
	}

	@GetMapping("get-room-by-id")
	public ResponseEntity<ApiResponse> getRoomById(
			@NotBlank(message = "The given id must not be null") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format") @RequestParam String param) {
		return new ResponseEntity<ApiResponse>(chatRoomService.getRoomById(param), HttpStatus.OK);
	}

	@GetMapping("/get-all-rooms")
	public ResponseEntity<ApiResponse> getAllRooms() {
		ApiResponse allRooms = chatRoomService.getAllRooms();
		System.err.println("------------ " + allRooms);
		return ResponseEntity.ok(allRooms);
	}

	@PutMapping("/update-room")
	public ResponseEntity<ApiResponse> updateRoomName(
			@NotBlank(message = "The given id must not be null") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format") @RequestParam String id,
			@NotBlank(message = "The given name must not be empty and null") @RequestParam String name) {
		return ResponseEntity.ok(chatRoomService.updateRoomName(id, name));
	}

	@PatchMapping("/add-room-member")
	public ResponseEntity<ApiResponse> addRoomMember(
			@NotBlank(message = "The given id must not be null") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format") @RequestParam String id,
			@RequestParam List<@NotBlank(message = "The given id must not be null") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format") String> userId) {
		return ResponseEntity.ok(chatRoomService.addRoomMember(id, userId));
	}

//	@DeleteMapping("/remove-room-member")
//	public ResponseEntity<ApiResponse> removeRoomMember(
//			@NotBlank(message = "The given id must not be null") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format") @RequestParam String id,
//			@NotBlank(message = "The given id must not be null") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format") @RequestParam String userId) {
//		return ResponseEntity.ok(chatRoomService.removeRoomMember(id, userId));
//	}

	@GetMapping("/get-all-rooms-by-user")
	public ResponseEntity<ApiResponse> getRoomsForUser(
			@NotBlank(message = "The given id must not be null") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format") @RequestParam String param) {
		return ResponseEntity.ok(chatRoomService.getRoomsForUser(param));
	}

}
