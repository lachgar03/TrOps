package com.TrOps.mvp.dashboard.service;

import com.TrOps.mvp.client.model.Client;
import com.TrOps.mvp.client.repository.ClientRepository;
import com.TrOps.mvp.dashboard.dto.DashboardSummaryDTO;
import com.TrOps.mvp.dashboard.dto.DashboardSummaryDTO.*;
import com.TrOps.mvp.mission.model.Mission;
import com.TrOps.mvp.mission.model.MissionStatus;
import com.TrOps.mvp.mission.model.ProfitabilityScore;
import com.TrOps.mvp.mission.repository.MissionRepository;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final MissionRepository missionRepository;
    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;

    public DashboardSummaryDTO getSummary() {
        UUID companyId = getCurrentCompanyId();

        // --- Financial KPIs (single queries, no N+1) ---
        BigDecimal totalRevenue = missionRepository.sumRevenuesByCompanyId(companyId);
        BigDecimal totalCosts   = missionRepository.sumCostsByCompanyId(companyId);
        BigDecimal netProfit    = missionRepository.sumProfitByCompanyId(companyId);

        // --- Count KPIs ---
        long activeMissions = missionRepository.countByCompanyId(companyId);
        long totalVehicles  = vehicleRepository.countByCompanyId(companyId);
        long vehiclesUnderMaintenance = vehicleRepository.countByCompanyIdAndIsUnderMaintenance(companyId, true);
        long totalClients   = clientRepository.findAllByCompanyId(companyId).size();
        long missionsAtLossCount = missionRepository.countByCompanyIdAndProfitabilityScore(
                companyId, ProfitabilityScore.LOSS);

        // --- Missions at loss (top 5 worst) ---
        List<MissionAtLossDTO> missionsAtLoss = missionRepository
                .findMissionsAtLoss(companyId, PageRequest.of(0, 5))
                .stream()
                .map(m -> new MissionAtLossDTO(
                        m.getId().toString(),
                        m.getVehicle().getRegistrationNumber(),
                        m.getClient().getName(),
                        m.getRevenues(),
                        m.getCosts(),
                        m.getProfit(),
                        m.getProfitMargin()
                ))
                .toList();

        // --- Top vehicles by profit ---
        List<VehicleInsightDTO> topVehicles = buildTopVehicles(companyId);

        // --- Top clients by profit ---
        List<ClientInsightDTO> topClients = buildTopClients(companyId);

        return new DashboardSummaryDTO(
                totalRevenue,
                totalCosts,
                netProfit,
                activeMissions,
                totalVehicles,
                vehiclesUnderMaintenance,
                totalClients,
                missionsAtLossCount,
                topVehicles,
                topClients,
                missionsAtLoss
        );
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private List<VehicleInsightDTO> buildTopVehicles(UUID companyId) {
        List<Mission> allMissions = missionRepository
                .findAllByCompanyId(companyId, PageRequest.of(0, 5000))
                .getContent();

        Map<UUID, List<Mission>> missionsByVehicle = allMissions.stream()
                .collect(Collectors.groupingBy(m -> m.getVehicle().getId()));

        return missionsByVehicle.entrySet().stream()
                .map(entry -> {
                    List<Mission> vMissions = entry.getValue();
                    String regNumber = vMissions.get(0).getVehicle().getRegistrationNumber();
                    BigDecimal totalRevenues = vMissions.stream()
                            .map(Mission::getRevenues)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalCosts = vMissions.stream()
                            .map(Mission::getCosts)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalProfit = vMissions.stream()
                            .map(Mission::getProfit)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new VehicleInsightDTO(
                            entry.getKey().toString(),
                            regNumber,
                            totalRevenues,
                            totalCosts,
                            totalProfit
                    );
                })
                .sorted(Comparator.comparing(VehicleInsightDTO::totalProfit).reversed())
                .limit(5)
                .toList();
    }

    private List<ClientInsightDTO> buildTopClients(UUID companyId) {
        List<Client> clients = clientRepository.findAllByCompanyId(companyId);
        List<Mission> allMissions = missionRepository
                .findAllByCompanyId(companyId, PageRequest.of(0, 5000))
                .getContent();

        Map<UUID, List<Mission>> missionsByClient = allMissions.stream()
                .collect(Collectors.groupingBy(m -> m.getClient().getId()));

        return clients.stream()
                .map(c -> {
                    List<Mission> cMissions = missionsByClient.getOrDefault(c.getId(), List.of());
                    BigDecimal totalRevenues = cMissions.stream()
                            .map(Mission::getRevenues)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalCosts = cMissions.stream()
                            .map(Mission::getCosts)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalProfit = cMissions.stream()
                            .map(Mission::getProfit)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new ClientInsightDTO(
                            c.getId().toString(),
                            c.getName(),
                            totalRevenues,
                            totalCosts,
                            totalProfit
                    );
                })
                .sorted(Comparator.comparing(ClientInsightDTO::totalProfit).reversed())
                .limit(5)
                .toList();
    }

    private UUID getCurrentCompanyId() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getCompanyId();
    }
}
