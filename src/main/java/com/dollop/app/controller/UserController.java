package com.dollop.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dollop.app.request.UserUpdateRequest;
import com.dollop.app.response.ApiResponse;
import com.dollop.app.service.IUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@Validated
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {
	@Autowired
	private IUserService userService;

	@GetMapping("/get-current-user")
	public ResponseEntity<ApiResponse> getCurrentUserById() {
		return ResponseEntity.ok(userService.getCurrentUserById());
	}

	@PutMapping("/update-user")
	public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody UserUpdateRequest param) {
		return ResponseEntity.ok(userService.updateUser(param));
	}

//	@GetMapping("/delete-user")
//	public String getMethodName(@RequestParam String param) {
//		return new String();
//	}
	@GetMapping("/get-all-users")
	public ResponseEntity<ApiResponse> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

}
