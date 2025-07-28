package com.dollop.app.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dollop.app.entity.User;
import com.dollop.app.exception.ResourceAlreadyExistsException;
import com.dollop.app.exception.ResourceNotFoundException;
import com.dollop.app.repo.IUserRepo;
import com.dollop.app.request.LoginRequest;
import com.dollop.app.request.UserRequest;
import com.dollop.app.request.UserUpdateRequest;
import com.dollop.app.response.ApiResponse;
import com.dollop.app.response.UserResponse;
import com.dollop.app.service.IUserService;
import com.dollop.app.utils.AppUtils;
import com.dollop.app.utils.ErrorConstant;
import com.dollop.app.utils.JwtUtil;

@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

	private IUserRepo userRepo;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private JwtUtil jwtUtil;
	private AuthenticationManager authenticationManager;
	@Autowired
	private AppUtils appUtils;

	public UserServiceImpl(IUserRepo userRepo, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil,
			@Lazy AuthenticationManager authenticationManager) {
		super();
		this.userRepo = userRepo;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.jwtUtil = jwtUtil;
		this.authenticationManager = authenticationManager;
	}

	@Override
	public ApiResponse createUser(UserRequest userRequest) {
		if (userRepo.existsByEmail(userRequest.getEmail()) || userRepo.existsByUsername(userRequest.getUsername())) {
			throw new ResourceNotFoundException(ErrorConstant.USER_NOT_FOUND);
		}

		User user = User.builder().email(userRequest.getEmail())
				.password(bCryptPasswordEncoder.encode(userRequest.getPassword())).name(userRequest.getName())
				.username(userRequest.getUsername()).build();
		userRepo.save(user);
		return ApiResponse.builder().message("User Successfully Registered!").build();
	}

	@Override
	public UserDetails loadUserByUsername(String email) {
		User user = findByEmail(email);
		System.err.println("find by email---->  " + user);
		List<GrantedAuthority> authorities = Collections.emptyList();
		return new org.springframework.security.core.userdetails.User(email, user.getPassword(), authorities);

	}

	@Override
	public ApiResponse login(LoginRequest loginRequest) {
		System.err.println(loginRequest);
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		// Ab direct authenticated user details le lo
		UserDetails userDetails = loadUserByUsername(loginRequest.getEmail());

		String token = jwtUtil.generateToken(userDetails.getUsername(), findByEmail(loginRequest.getEmail()).getId());
		System.err.println("token   " + token);
		return ApiResponse.builder().message("User Successfully Loged In!").response(Map.of("token", token)).build();
	}

	@Override
	public User findByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public List<UserResponse> userToUserResponse(List<User> user) {
		return user.stream().map(this::userToUserResponse).toList();
	}

	@Override
	public UserResponse userToUserResponse(User user) {
		return UserResponse.builder().id(user.getId()).name(user.getName()).username(user.getUsername())
				.email(user.getEmail()).profilePicUrl(user.getProfilePicUrl()).statusType(user.getStatusType())
				.lastSeen(Timestamp.valueOf(user.getLastSeen())).build();
	}

	@Override
	public ApiResponse getCurrentUserById() {

		return ApiResponse.builder().response(Map.of("user", userToUserResponse(findById(appUtils.getIdByToken()))))
				.build();
	}

	@Override
	public User getCurrentUser() {

		return findById(appUtils.getIdByToken());
	}

	@Override
	public User findById(String id) {

		return userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorConstant.USER_NOT_FOUND));
	}

	@Override
	public ApiResponse getAllUsers() {
		User currentUser = getCurrentUser();
		return ApiResponse.builder().message("All User!")
				.response(Map.of("users", userToUserResponse(userRepo.findByIdNot(currentUser.getId())))).build();
	}

	@Override
	public ApiResponse updateUser(UserUpdateRequest updatedUser) {
		String id = appUtils.getIdByToken();
		User user = findById(id);
		if (userRepo.existsByEmailAndIdNot(user.getEmail(), id)) {
			throw new ResourceAlreadyExistsException(ErrorConstant.USER_ALREADY_EXISTS);
		}
		user.setName(updatedUser.getName() == null ? user.getName() : updatedUser.getName());
		user.setEmail(updatedUser.getEmail() == null ? user.getEmail() : updatedUser.getEmail());

		userRepo.save(user);
		return ApiResponse.builder().message("User Updated Successfully !").build();
	}

	@Override
	public ApiResponse deleteUser(String userId) {

		return null;
	}

	@Override
	public UserResponse setLastSeen(String email) {
		User user = findByEmail(email);
		user.setLastSeen(LocalDateTime.now());
		return userToUserResponse(save(user));
	}

	private User save(User user) {
		return userRepo.save(user);

	}

}
