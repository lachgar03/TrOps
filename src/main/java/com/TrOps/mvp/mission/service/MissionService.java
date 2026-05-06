package com.TrOps.mvp.mission.service;

import com.TrOps.mvp.client.model.Client;
import com.TrOps.mvp.client.service.ClientService;
import com.TrOps.mvp.common.exception.BusinessRuleException;
import com.TrOps.mvp.common.exception.ResourceNotFoundException;
import com.TrOps.mvp.mission.dto.MissionRequestDTO;
import com.TrOps.mvp.mission.dto.MissionResponseDTO;
import com.TrOps.mvp.mission.dto.MissionStatusUpdateDTO;
import com.TrOps.mvp.mission.dto.MissionSummaryDTO;
import com.TrOps.mvp.mission.model.Mission;
import com.TrOps.mvp.mission.model.MissionStatus;
import com.TrOps.mvp.mission.repository.MissionRepository;
import com.TrOps.mvp.mission.service.strategy.ProfitabilityResult;
import com.TrOps.mvp.mission.service.strategy.ProfitabilityStrategy;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.vehicle.model.Vehicle;
import com.TrOps.mvp.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService implements MissionQueryService {

    private final MissionRepository missionRepository;
    private final VehicleRepository vehicleRepository;
    private final ClientService clientService;
    private final ProfitabilityStrategy profitabilityStrategy;

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    @Transactional
    public MissionResponseDTO createMission(MissionRequestDTO request) {
        UUID companyId = getCurrentCompanyId();

        // 1. Resolve Vehicle directly via VehicleRepository (avoids circular dependency with VehicleService)
        Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(request.vehicleId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable pour cette entreprise"));

        if (vehicle.isUnderMaintenance()) {
            throw new BusinessRuleException("Impossible d'assigner la mission : Le véhicule est en maintenance");
        }

        // 2. Resolve Client through ClientService
        Client client = clientService.resolveClient(request.clientId(), companyId);

        // 3. Build mission
        Mission mission = new Mission();
        mission.setCompanyId(companyId);
        mission.setVehicle(vehicle);
        mission.setClient(client);
        mission.setRevenues(request.revenues());
        mission.setCosts(request.costs());

        // 4. Compute profitability (Strategy Pattern)
        ProfitabilityResult result = profitabilityStrategy.calculate(request.revenues(), request.costs());
        mission.setProfit(result.profit());
        mission.setProfitMargin(result.margin());
        mission.setProfitabilityScore(result.score());

        return mapToResponseDTO(missionRepository.saveAndFlush(mission));
    }

    public Page<MissionResponseDTO> getAllMissions(Pageable pageable) {
        UUID companyId = getCurrentCompanyId();
        return missionRepository.findAllByCompanyId(companyId, pageable)
                .map(this::mapToResponseDTO);
    }

    public Page<MissionResponseDTO> getMissionsByStatus(MissionStatus status, Pageable pageable) {
        UUID companyId = getCurrentCompanyId();
        return missionRepository.findAllByCompanyIdAndStatus(companyId, status, pageable)
                .map(this::mapToResponseDTO);
    }

    public MissionResponseDTO getMissionById(UUID id) {
        UUID companyId = getCurrentCompanyId();
        Mission mission = missionRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission introuvable"));
        return mapToResponseDTO(mission);
    }

    @Transactional
    public MissionResponseDTO updateStatus(UUID id, MissionStatusUpdateDTO request) {
        UUID companyId = getCurrentCompanyId();
        Mission mission = missionRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission introuvable"));

        validateStatusTransition(mission.getStatus(), request.status());
        mission.setStatus(request.status());

        return mapToResponseDTO(missionRepository.save(mission));
    }

    // -------------------------------------------------------------------------
    // MissionQueryService — used by VehicleService & DashboardService
    // Package-level: no cross-module repo access needed
    // -------------------------------------------------------------------------

    @Override
    public List<MissionSummaryDTO> getMissionsByVehicle(UUID vehicleId, UUID companyId) {
        return missionRepository.findAllByVehicleIdAndCompanyId(vehicleId, companyId)
                .stream()
                .map(this::mapToSummaryDTO)
                .toList();
    }

    @Override
    public List<MissionSummaryDTO> getMissionsByClient(UUID clientId, UUID companyId) {
        return missionRepository.findAllByClientIdAndCompanyId(clientId, companyId)
                .stream()
                .map(this::mapToSummaryDTO)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void validateStatusTransition(MissionStatus current, MissionStatus next) {
        // Allowed transitions
        boolean valid = switch (current) {
            case PLANNED     -> Set.of(MissionStatus.IN_PROGRESS, MissionStatus.CANCELLED).contains(next);
            case IN_PROGRESS -> Set.of(MissionStatus.COMPLETED, MissionStatus.CANCELLED).contains(next);
            case COMPLETED, CANCELLED -> false; // terminal states
        };

        if (!valid) {
            throw new BusinessRuleException(
                    "Transition de statut invalide : " + current + " → " + next);
        }
    }

    private UUID getCurrentCompanyId() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getCompanyId();
    }

    private MissionResponseDTO mapToResponseDTO(Mission mission) {
        return new MissionResponseDTO(
                mission.getId(),
                mission.getVehicle().getId(),
                mission.getVehicle().getRegistrationNumber(),
                mission.getClient().getId(),
                mission.getClient().getName(),
                mission.getRevenues(),
                mission.getCosts(),
                mission.getProfit(),
                mission.getStatus().name(),
                mission.getProfitMargin(),
                mission.getProfitabilityScore() != null ? mission.getProfitabilityScore().name() : null,
                mission.getCreatedAt()
        );
    }

    private MissionSummaryDTO mapToSummaryDTO(Mission mission) {
        return new MissionSummaryDTO(
                mission.getId(),
                mission.getVehicle().getId(),
                mission.getClient().getId(),
                mission.getRevenues(),
                mission.getCosts(),
                mission.getProfit(),
                mission.getStatus().name()
        );
    }
}

