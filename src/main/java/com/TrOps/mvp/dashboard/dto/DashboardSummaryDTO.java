package com.TrOps.mvp.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryDTO(
        // KPIs financiers globaux
        BigDecimal totalRevenue,
        BigDecimal totalCosts,
        BigDecimal netProfit,

        // Compteurs
        long activeMissions,
        long totalVehicles,
        long vehiclesUnderMaintenance,
        long totalClients,
        long missionsAtLossCount,

        // Insights
        List<VehicleInsightDTO> topVehicles,
        List<ClientInsightDTO> topClients,
        List<MissionAtLossDTO> missionsAtLoss
) {

    public record VehicleInsightDTO(
            String vehicleId,
            String registrationNumber,
            BigDecimal totalRevenues,
            BigDecimal totalCosts,
            BigDecimal totalProfit
    ) {}

    public record ClientInsightDTO(
            String clientId,
            String clientName,
            BigDecimal totalRevenues,
            BigDecimal totalCosts,
            BigDecimal totalProfit
    ) {}

    public record MissionAtLossDTO(
            String missionId,
            String vehicleRegistration,
            String clientName,
            BigDecimal revenues,
            BigDecimal costs,
            BigDecimal profit,
            BigDecimal profitMargin
    ) {}
}
