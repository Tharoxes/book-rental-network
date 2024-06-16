package com.tharoxes.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

	@NotEmpty(message = "Firtname is mandatory")
	@NotBlank(message = "Firtname is mandatory")
	private String firstname;
	@NotEmpty(message = "Firtname is mandatory")
	@NotBlank(message = "Firtname is mandatory")
	private String lastname;
	@Email(message = "Email is not formatted")
	@NotEmpty(message = "Firtname is mandatory")
	@NotBlank(message = "Firtname is mandatory")
	private String email;
	@NotEmpty(message = "Firtname is mandatory")
	@NotBlank(message = "Firtname is mandatory")
	@Size(min = 8, message = "Password should be 8 characters long minimum")
	private String password;
}
