package br.com.jobson.controller.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDto(
        @NotBlank(message = "The firstName is required") String firstName,
        @NotBlank(message = "The lastName is required") String lastName,
        @Email(message = "Invalid email") @NotBlank(message = "The email is required") String email,
        @NotBlank(message = "The password is required") String password,
        @NotBlank(message = "The confirmPassword is required") String confirmPassword
) {
}
