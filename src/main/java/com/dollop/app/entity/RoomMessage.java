package com.dollop.app.entity;

import com.dollop.app.enums.MessageStatus;
import com.dollop.app.enums.MessageType;
import com.dollop.app.enums.ServerType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@SuperBuilder
@RequiredArgsConstructor
public class RoomMessage extends Auditable {

	public RoomMessage(ServerType server, String message) {
		this.serverType = server;
		this.content = message;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@ManyToOne
	@JoinColumn(name = "room_id")
	private ChatRoom room;

	@ManyToOne
	@JoinColumn(name = "sender_id")
	private User sender;
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;
	@ManyToOne
	private RoomMessage replayMessage;
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private MessageStatus statusType = MessageStatus.SENT;
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private MessageType type = MessageType.TEXT;
	@Enumerated(EnumType.STRING)
	private ServerType serverType;

}
