package com.TrOps.mvp.maintenance.service;

import com.TrOps.mvp.common.exception.BusinessRuleException;
import com.TrOps.mvp.common.exception.ResourceNotFoundException;
import com.TrOps.mvp.maintenance.dto.MaintenanceRequestDTO;
import com.TrOps.mvp.maintenance.dto.MaintenanceResponseDTO;
import com.TrOps.mvp.maintenance.dto.MaintenanceStatusUpdateDTO;
import com.TrOps.mvp.maintenance.repository.MaintenanceRepository;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.vehicle.model.MaintenanceLog;
import com.TrOps.mvp.vehicle.model.MaintenanceStatus;
import com.TrOps.mvp.vehicle.model.Vehicle;
import com.TrOps.mvp.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public MaintenanceResponseDTO createMaintenance(MaintenanceRequestDTO request) {
        UUID companyId = getCurrentCompanyId();

        Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(request.vehicleId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        MaintenanceLog log = new MaintenanceLog();
        log.setCompanyId(companyId);
        log.setVehicle(vehicle);
        log.setDescription(request.description());
        log.setCost(request.cost());
        log.setMileageAtMaintenance(request.mileageAtMaintenance());
        log.setMaintenanceDate(request.maintenanceDate());
        log.setStatus(MaintenanceStatus.SCHEDULED);

        // Mark vehicle as under maintenance
        vehicle.setUnderMaintenance(true);
        vehicleRepository.save(vehicle);

        return mapToResponseDTO(maintenanceRepository.save(log));
    }

    public Page<MaintenanceResponseDTO> getAllMaintenanceLogs(Pageable pageable) {
        UUID companyId = getCurrentCompanyId();
        return maintenanceRepository.findAllByCompanyId(companyId, pageable)
                .map(this::mapToResponseDTO);
    }

    public Page<MaintenanceResponseDTO> getByVehicle(UUID vehicleId, Pageable pageable) {
        UUID companyId = getCurrentCompanyId();
        return maintenanceRepository.findAllByCompanyIdAndVehicleId(companyId, vehicleId, pageable)
                .map(this::mapToResponseDTO);
    }

    public MaintenanceResponseDTO getById(UUID id) {
        UUID companyId = getCurrentCompanyId();
        MaintenanceLog log = maintenanceRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance introuvable"));
        return mapToResponseDTO(log);
    }

    @Transactional
    public MaintenanceResponseDTO updateStatus(UUID id, MaintenanceStatusUpdateDTO request) {
        UUID companyId = getCurrentCompanyId();
        MaintenanceLog log = maintenanceRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance introuvable"));

        validateStatusTransition(log.getStatus(), request.status());
        log.setStatus(request.status());

        // If completed, release the vehicle
        if (request.status() == MaintenanceStatus.COMPLETED) {
            Vehicle vehicle = log.getVehicle();
            vehicle.setUnderMaintenance(false);
            vehicleRepository.save(vehicle);
        }

        return mapToResponseDTO(maintenanceRepository.save(log));
    }

    private void validateStatusTransition(MaintenanceStatus current, MaintenanceStatus next) {
        boolean valid = switch (current) {
            case SCHEDULED -> next == MaintenanceStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == MaintenanceStatus.COMPLETED;
            case COMPLETED -> false;
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

    private MaintenanceResponseDTO mapToResponseDTO(MaintenanceLog log) {
        return new MaintenanceResponseDTO(
                log.getId(),
                log.getVehicle().getId(),
                log.getVehicle().getRegistrationNumber(),
                log.getDescription(),
                log.getCost(),
                log.getMileageAtMaintenance(),
                log.getStatus().name(),
                log.getMaintenanceDate(),
                log.getCreatedAt()
        );
    }
}
