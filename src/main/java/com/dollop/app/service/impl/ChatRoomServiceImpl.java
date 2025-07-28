package com.dollop.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dollop.app.entity.ChatRoom;
import com.dollop.app.entity.RoomMember;
import com.dollop.app.entity.User;
import com.dollop.app.exception.ResourceNotFoundException;
import com.dollop.app.module.SocketModule;
import com.dollop.app.repo.IChatRoomRepo;
import com.dollop.app.request.ChatRoomRequest;
import com.dollop.app.response.ApiResponse;
import com.dollop.app.response.ChatRoomResponse;
import com.dollop.app.service.IChatRoomService;
import com.dollop.app.service.IRoomMemberService;
import com.dollop.app.service.IUserService;
import com.dollop.app.utils.ErrorConstant;

@Service
public class ChatRoomServiceImpl implements IChatRoomService {
	@Autowired
	private IUserService userService;
	@Autowired
	private IChatRoomRepo chatRoomRepo;
	@Autowired
	private IRoomMemberService roomMemberService;
	@Autowired
	@Lazy
	private SocketModule socketModule;

	@Override
	public ApiResponse createRoom(ChatRoomRequest roomRequest) {
		System.err.println("===================>     " + roomRequest);
		if (!roomRequest.getIsGroup()) {
			Optional<ChatRoom> chatRoom1 = chatRoomRepo.findOneToOneChatRoomBetween(
					userService.getCurrentUser().getId(), roomRequest.getMemberIds().get(0));
			if (chatRoom1 != null && chatRoom1.isPresent())
				return ApiResponse.builder().message("Already Found Chat Room!")
						.response(Map.of("chatRoom", chatRoom1.get())).build();
		}
		User admin = userService.getCurrentUser();
		ChatRoom chatRoom = ChatRoom.builder().name(roomRequest.getRoomName()).createdBy(admin)
				.isGroup(roomRequest.getIsGroup()).build();
		List<RoomMember> list = new ArrayList<>();
		for (String id : roomRequest.getMemberIds()) {
			User user = userService.findById(id);
			list.add(RoomMember.builder().chatRoom(chatRoom).user(user).isAvailable(true).build());
		}
		list.add(RoomMember.builder().user(admin).chatRoom(chatRoom).isAdmin(true).isAvailable(true).build());
		chatRoom.setMember(list);
		socketModule.createRoom(chatRoomtoChatRoomResponse(chatRoom));
		return ApiResponse.builder().message("Chat Room Created Successfully!")
				.response(Map.of("chatRoom", chatRoomtoChatRoomResponse(chatRoomRepo.save(chatRoom)))).build();

	}

	@Override
	public ApiResponse getRoomById(String id) {
		ChatRoom chatRoom = findRoomById(id);
		return ApiResponse.builder().message("Found Chat Room!")
				.response(Map.of("chatRoom", chatRoomtoChatRoomResponse(chatRoom))).build();
	}

	@Override
	public ChatRoom findRoomById(String id) {

		return chatRoomRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorConstant.CHAT_ROOM_NOT_FOUND));
	}

	@Override
	public ApiResponse getAllRooms() {
		User user = userService.getCurrentUser();
		List<ChatRoom> list = chatRoomRepo.findUserChatRoomsSortedByLastMessage(user.getId());
		if (list == null || list.isEmpty())
			throw new ResourceNotFoundException(ErrorConstant.CHAT_ROOM_NOT_FOUND);
		System.err.println(list);
		return ApiResponse.builder().response(Map.of("chatRooms", chatRoomtoChatRoomResponse(list))).build();
	}

	@Override
	public ApiResponse updateRoomName(String id, String name) {
		ChatRoom chatRoom = findRoomById(id);
		chatRoom.setName(name);
		chatRoomRepo.save(chatRoom);
		return ApiResponse.builder().message("Room Name Successfully Changed!").build();
	}

	@Override
	public ApiResponse addRoomMember(String id, List<String> userId) {
		ChatRoom chatRoom = findRoomById(id);

		userId.forEach((uid) -> {
			if (!chatRoom.isGroup())
				throw new ResourceNotFoundException(ErrorConstant.ROOM_NOT_FOUND);

			User user = userService.findById(uid);
			RoomMember roomMember = null;
			if (roomMemberService.isUserMemberOfChatRoom(id, uid)) {
				roomMember = roomMemberService.findUserMemberOfChatRoom(id, uid);
				roomMember.setIsAvailable(true);
				roomMemberService.save(roomMember);
			} else {
				roomMember = RoomMember.builder().user(user).chatRoom(chatRoom).isAvailable(true).isAdmin(false)
						.build();

				chatRoom.getMember().add(roomMember);
				chatRoomRepo.save(chatRoom);
			}
		});
		ChatRoom chatRoom1 = findRoomById(id);
		socketModule.addMemberEvent(chatRoomtoChatRoomResponse(chatRoom1));
		return ApiResponse.builder().message("Member added to group succcessfully !")
				.response(Map.of("room", chatRoomtoChatRoomResponse(chatRoom1))).build();
	}

	@Override
	@Transactional
	public Map removeRoomMember(List<String> ids, String roomId) {
		ChatRoom chatRoom = findRoomById(roomId);
		List<String> emails = new ArrayList<>();

		for (String id : ids) {
			RoomMember roomMember = roomMemberService.findById(id);
			roomMember.setIsAvailable(false);
			roomMember = roomMemberService.save(roomMember);
			System.err.println(" = = = = = = = = = = = = " + roomMember.getIsAvailable());
//			chatRoom.getMember().remove(roomMember);
			emails.add(roomMember.getUser().getEmail());
		}

		chatRoom = findRoomById(roomId);
		return Map.of("chatRoom", chatRoomtoChatRoomResponse(chatRoom), "emails", emails);
	}

	@Override
	public ApiResponse getRoomsForUser(String userId) {
		userService.findById(userId);
		List<ChatRoom> chatRooms = chatRoomRepo.findUserChatRoomsSortedByLastMessage(userId);
		return ApiResponse.builder().message("Chat Rooms List!")
				.response(Map.of("chatRooms", chatRoomtoChatRoomResponse(chatRooms))).build();
	}

	@Override
	public ChatRoomResponse chatRoomtoChatRoomResponse(ChatRoom chatRoom) {
		return ChatRoomResponse.builder().id(chatRoom.getId()).name(chatRoom.getName()).isGroup(chatRoom.isGroup())
				.createdBy(userService.userToUserResponse(chatRoom.getCreatedBy()))
				.members(roomMemberService.roomMemberToRoomMemberResponse(chatRoom.getMember())).build();
	}

	@Override
	public List<ChatRoomResponse> chatRoomtoChatRoomResponse(List<ChatRoom> chatRoom) {
		return chatRoom.stream().map(this::chatRoomtoChatRoomResponse).toList();
	}

}
