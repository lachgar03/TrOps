package com.TrOps.mvp.maintenance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record MaintenanceResponseDTO(
        UUID id,
        UUID vehicleId,
        String vehicleRegistrationNumber,
        String description,
        BigDecimal cost,
        Integer mileageAtMaintenance,
        String status,
        LocalDate maintenanceDate,
        LocalDateTime createdAt
) {}
