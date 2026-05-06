package com.TrOps.mvp.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryDTO(
        // KPIs financiers globaux
        BigDecimal totalRevenue,
        BigDecimal totalCosts,
        BigDecimal netProfit,
        long totalMissions,
        long missionsAtLossCount,

        // Insights
        List<VehicleInsightDTO> topVehicles,
        List<ClientInsightDTO> topClients,
        List<MissionAtLossDTO> missionsAtLoss
) {

    public record VehicleInsightDTO(
            String vehicleId,
            String registrationNumber,
            BigDecimal totalRevenue,
            BigDecimal netProfit
    ) {}

    public record ClientInsightDTO(
            String clientId,
            String clientName,
            long totalMissions,
            BigDecimal totalRevenue
    ) {}

    public record MissionAtLossDTO(
            String missionId,
            String vehicleRegistration,
            String clientName,
            BigDecimal revenues,
            BigDecimal costs,
            BigDecimal profit
    ) {}
}
