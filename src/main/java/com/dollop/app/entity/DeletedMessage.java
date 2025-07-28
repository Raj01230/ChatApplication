package com.dollop.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DeletedMessage extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	// The message that was deleted
	@ManyToOne
	@JoinColumn(name = "message_id")
	private RoomMessage message;

	// The user who deleted this message
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	@Builder.Default
	private Boolean visible = true;
}
