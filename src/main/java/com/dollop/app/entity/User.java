package com.dollop.app.entity;

import java.time.LocalDateTime;

import com.dollop.app.enums.UserStatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class User extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	@Column(nullable = false)
	private String name;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;
	@Builder.Default
	private String profilePicUrl = "https://res.cloudinary.com/dvhloff4c/image/upload/v1742537457/free-user-icon-3296-thumb_qteroi.png";
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private UserStatusType statusType = UserStatusType.OFFLINE;
	@Builder.Default
	private LocalDateTime lastSeen = LocalDateTime.now();
}
