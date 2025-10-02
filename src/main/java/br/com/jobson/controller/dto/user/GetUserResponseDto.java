package br.com.jobson.controller.dto.user;

import java.util.Optional;
import java.util.UUID;

public record GetUserResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Optional<String> role
) {
}
