package com.TrOps.mvp.maintenance.dto;

import com.TrOps.mvp.vehicle.model.MaintenanceStatus;
import jakarta.validation.constraints.NotNull;

public record MaintenanceStatusUpdateDTO(
        @NotNull(message = "Le statut est obligatoire")
        MaintenanceStatus status
) {}
