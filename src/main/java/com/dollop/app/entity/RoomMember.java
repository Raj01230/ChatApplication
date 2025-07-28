package com.dollop.app.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
//@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMember extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@EqualsAndHashCode.Include
	private String id;

	@ManyToOne
	@JoinColumn(name = "room_id")
	private ChatRoom chatRoom;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonManagedReference
	private User user;
	@Builder.Default
	private Boolean isAdmin = false; // only for group chat

	private Boolean isAvailable;

	@Builder.Default
	private Timestamp lastSeen = new Timestamp(System.currentTimeMillis());
}
