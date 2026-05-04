package com.TrOps.mvp.vehicle.service;

import com.TrOps.mvp.common.exception.ResourceNotFoundException;
import com.TrOps.mvp.mission.model.Mission;
import com.TrOps.mvp.mission.repository.MissionRepository;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.vehicle.dto.VehicleFinancialSummaryDTO;
import com.TrOps.mvp.vehicle.dto.VehicleRequestDTO;
import com.TrOps.mvp.vehicle.dto.VehicleResponseDTO;
import com.TrOps.mvp.vehicle.model.Vehicle;
import com.TrOps.mvp.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final MissionRepository missionRepository;

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

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToResponseDTO(savedVehicle);
    }

    public List<VehicleResponseDTO> getAllVehicles() {
        UUID companyId = getCurrentCompanyId();
        return vehicleRepository.findAllByCompanyId(companyId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public VehicleResponseDTO getVehicleById(UUID id) {
        UUID companyId = getCurrentCompanyId();
        Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));
        return mapToResponseDTO(vehicle);
    }

    public VehicleFinancialSummaryDTO getVehicleFinancialSummary(UUID id) {
        UUID companyId = getCurrentCompanyId();
        
        // Ensure vehicle exists
        Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        // Retrieve all missions for this vehicle
        List<Mission> missions = missionRepository.findAllByVehicleIdAndCompanyId(id, companyId);

        // Aggregate financial data
        BigDecimal totalRevenues = missions.stream()
                .map(Mission::getRevenues)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCosts = missions.stream()
                .map(Mission::getCosts)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfit = missions.stream()
                .map(Mission::getProfit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new VehicleFinancialSummaryDTO(
                vehicle.getId(),
                vehicle.getRegistrationNumber(),
                totalRevenues,
                totalCosts,
                totalProfit
        );
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
