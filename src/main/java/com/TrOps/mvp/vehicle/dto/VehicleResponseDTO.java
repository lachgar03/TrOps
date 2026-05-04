package com.TrOps.mvp.vehicle.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record VehicleResponseDTO(
        UUID id,
        String registrationNumber,
        String brand,
        String model,
        Integer currentMileage,
        boolean isUnderMaintenance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
