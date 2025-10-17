package br.com.jobson.controller.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @Email(message = "Invalid email") @NotBlank(message = "The email is required") String email,
        @NotBlank(message = "The password is required") String password
) {
}
