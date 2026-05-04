package com.TrOps.mvp.mission.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.UUID;

public record MissionRequestDTO(
        @NotNull(message = "Le véhicule est obligatoire")
        UUID vehicleId,
        
        @NotNull(message = "Le client est obligatoire")
        UUID clientId,
        
        @NotNull(message = "Les revenus sont obligatoires")
        @PositiveOrZero(message = "Les revenus doivent être positifs ou nuls")
        BigDecimal revenues,
        
        @NotNull(message = "Les coûts sont obligatoires")
        @PositiveOrZero(message = "Les coûts doivent être positifs ou nuls")
        BigDecimal costs
) {}
