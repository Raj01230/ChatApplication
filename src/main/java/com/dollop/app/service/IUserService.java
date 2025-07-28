package com.dollop.app.service;

import java.util.List;

import com.dollop.app.entity.User;
import com.dollop.app.request.LoginRequest;
import com.dollop.app.request.UserRequest;
import com.dollop.app.request.UserUpdateRequest;
import com.dollop.app.response.ApiResponse;
import com.dollop.app.response.UserResponse;

public interface IUserService {
	ApiResponse createUser(UserRequest user);

	User findById(String id);

	ApiResponse getAllUsers();

	ApiResponse deleteUser(String userId);

	ApiResponse login(LoginRequest loginRequest);

	ApiResponse getCurrentUserById();

	ApiResponse updateUser(UserUpdateRequest updatedUser);

	List<UserResponse> userToUserResponse(List<User> user);

	UserResponse userToUserResponse(User user);

	User getCurrentUser();

	User findByEmail(String email);

	UserResponse setLastSeen(String email);

//	List<User> searchUsers(String keyword);
}
