package com.TrOps.mvp.mission.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lightweight DTO used by other modules (Vehicle, Dashboard)
 * to access mission financial data without coupling to the full response DTO.
 */
public record MissionSummaryDTO(
        UUID id,
        UUID vehicleId,
        UUID clientId,
        BigDecimal revenues,
        BigDecimal costs,
        BigDecimal profit,
        String status
) {}
