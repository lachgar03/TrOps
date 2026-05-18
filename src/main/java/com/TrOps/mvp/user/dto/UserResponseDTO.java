package com.TrOps.mvp.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String role,
        LocalDateTime createdAt
) {}
