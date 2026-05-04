package com.TrOps.mvp.mission.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MissionResponseDTO(
        UUID id,
        UUID vehicleId,
        String vehicleRegistrationNumber,
        UUID clientId,
        String clientName,
        BigDecimal revenues,
        BigDecimal costs,
        BigDecimal profit,
        String status,
        BigDecimal profitMargin,
        String profitabilityScore,
        LocalDateTime createdAt
) {}
