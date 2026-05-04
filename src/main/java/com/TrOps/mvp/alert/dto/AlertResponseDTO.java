package com.TrOps.mvp.alert.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlertResponseDTO(
        UUID id,
        String type,
        String level,
        String status,
        String title,
        String description,
        String referenceId,
        LocalDateTime createdAt
) {
}
