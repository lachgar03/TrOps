package com.TrOps.mvp.maintenance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MaintenanceRequestDTO(
        @NotNull(message = "Le véhicule est obligatoire")
        UUID vehicleId,

        @NotBlank(message = "La description est obligatoire")
        String description,

        @PositiveOrZero(message = "Le coût doit être positif ou nul")
        BigDecimal cost,

        @NotNull(message = "Le kilométrage est obligatoire")
        Integer mileageAtMaintenance,

        @NotNull(message = "La date de maintenance est obligatoire")
        LocalDate maintenanceDate
) {}
