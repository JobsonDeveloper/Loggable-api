package br.com.jobson.controller.dto.user;

public record CreateUserDto(
        String firstName,
        String lastName,
        String email,
        String password,
        String confirmPassword
) {
}
