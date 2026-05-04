package com.TrOps.mvp.mission.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MissionRequestDTO(
        UUID vehicleId,
        UUID clientId,
        BigDecimal revenues,
        BigDecimal costs
) {}
