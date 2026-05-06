package com.TrOps.mvp.mission.dto;

import com.TrOps.mvp.mission.model.MissionStatus;
import jakarta.validation.constraints.NotNull;

public record MissionStatusUpdateDTO(
        @NotNull(message = "Le statut est obligatoire")
        MissionStatus status
) {}
