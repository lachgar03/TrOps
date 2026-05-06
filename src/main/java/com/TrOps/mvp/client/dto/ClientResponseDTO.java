package com.TrOps.mvp.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientResponseDTO(
        UUID id,
        String name,
        String email,
        String phone,
        LocalDateTime createdAt
) {}
