package com.dollop.app.request;

import com.dollop.app.annotation.Trimmed;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Trimmed
@JsonInclude(Include.NON_NULL)
public class UserUpdateRequest {
	@NotEmpty(message = "Name cannot be empty. Please provide a name.")
	private String name;

	@NotEmpty(message = "Email cannot be empty. Please provide a Email.")
	@Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z]+\\.[A-Za-z]{2,}$", message = "Invalid email. Use format: local@domain.com")
	private String email;
}
