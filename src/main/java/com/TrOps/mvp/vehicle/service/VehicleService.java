package com.TrOps.mvp.vehicle.service;

import com.TrOps.mvp.common.exception.ResourceNotFoundException;
import com.TrOps.mvp.mission.dto.MissionSummaryDTO;
import com.TrOps.mvp.mission.service.MissionQueryService;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.vehicle.dto.VehicleFinancialSummaryDTO;
import com.TrOps.mvp.vehicle.dto.VehicleRequestDTO;
import com.TrOps.mvp.vehicle.dto.VehicleResponseDTO;
import com.TrOps.mvp.vehicle.model.Vehicle;
import com.TrOps.mvp.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final MissionQueryService missionQueryService;

    @Transactional
    public VehicleResponseDTO createVehicle(VehicleRequestDTO request) {
        UUID companyId = getCurrentCompanyId();

        Vehicle vehicle = new Vehicle();
        vehicle.setCompanyId(companyId);
        vehicle.setRegistrationNumber(request.registrationNumber());
        vehicle.setBrand(request.brand());
        vehicle.setModel(request.model());
        vehicle.setCurrentMileage(request.currentMileage());
        vehicle.setUnderMaintenance(false);

        return mapToResponseDTO(vehicleRepository.save(vehicle));
    }

    public Page<VehicleResponseDTO> getAllVehicles(Pageable pageable) {
        UUID companyId = getCurrentCompanyId();
        return vehicleRepository.findAllByCompanyId(companyId, pageable)
                .map(this::mapToResponseDTO);
    }

    public VehicleResponseDTO getVehicleById(UUID id) {
        UUID companyId = getCurrentCompanyId();
        Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));
        return mapToResponseDTO(vehicle);
    }

    public VehicleFinancialSummaryDTO getVehicleFinancialSummary(UUID id) {
        UUID companyId = getCurrentCompanyId();

        Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        // Use MissionQueryService — no direct cross-module repository access
        List<MissionSummaryDTO> missions = missionQueryService.getMissionsByVehicle(id, companyId);

        BigDecimal totalRevenues = missions.stream()
                .map(MissionSummaryDTO::revenues)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCosts = missions.stream()
                .map(MissionSummaryDTO::costs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfit = missions.stream()
                .map(MissionSummaryDTO::profit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new VehicleFinancialSummaryDTO(
                vehicle.getId(),
                vehicle.getRegistrationNumber(),
                totalRevenues,
                totalCosts,
                totalProfit
        );
    }

    /**
     * Package-accessible method used by MissionService to resolve a vehicle
     * by ID within the correct tenant scope, without MissionService accessing
     * VehicleRepository directly.
     */
    public Vehicle resolveVehicle(UUID vehicleId, UUID companyId) {
        return vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable pour cette entreprise"));
    }

    private UUID getCurrentCompanyId() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getCompanyId();
    }

    private VehicleResponseDTO mapToResponseDTO(Vehicle vehicle) {
        return new VehicleResponseDTO(
                vehicle.getId(),
                vehicle.getRegistrationNumber(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getCurrentMileage(),
                vehicle.isUnderMaintenance(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }
}

