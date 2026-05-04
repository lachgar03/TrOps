package com.TrOps.mvp.vehicle.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record VehicleFinancialSummaryDTO(
        UUID vehicleId,
        String registrationNumber,
        BigDecimal totalRevenues,
        BigDecimal totalCosts,
        BigDecimal totalProfit
) {
}
