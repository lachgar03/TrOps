package com.TrOps.mvp.vehicle.service;

import com.TrOps.mvp.common.exception.ResourceNotFoundException;
import com.TrOps.mvp.mission.dto.MissionSummaryDTO;
import com.TrOps.mvp.mission.service.MissionQueryService;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.vehicle.dto.DocumentRequestDTO;
import com.TrOps.mvp.vehicle.dto.DocumentResponseDTO;
import com.TrOps.mvp.vehicle.dto.VehicleFinancialSummaryDTO;
import com.TrOps.mvp.vehicle.dto.VehicleRequestDTO;
import com.TrOps.mvp.vehicle.dto.VehicleResponseDTO;
import com.TrOps.mvp.vehicle.model.*;
import com.TrOps.mvp.vehicle.repository.VehicleDocumentRepository;
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
    private final VehicleDocumentRepository vehicleDocumentRepository;
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

    // ---- Document Management ----

    public List<DocumentResponseDTO> getDocumentsByVehicle(UUID vehicleId) {
        UUID companyId = getCurrentCompanyId();
        vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        return vehicleDocumentRepository.findAllByVehicleIdAndCompanyId(vehicleId, companyId)
                .stream()
                .map(this::mapDocToDTO)
                .toList();
    }

    @Transactional
    public DocumentResponseDTO addDocument(UUID vehicleId, DocumentRequestDTO request) {
        UUID companyId = getCurrentCompanyId();
        Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));

        VehicleDocument doc = switch (request.documentType()) {
            case INSURANCE -> {
                InsuranceDocument ins = new InsuranceDocument();
                ins.setPolicyNumber(request.documentNumber());
                yield ins;
            }
            case REGISTRATION -> {
                RegistrationDocument reg = new RegistrationDocument();
                reg.setChassisNumber(request.documentNumber());
                yield reg;
            }
            case TECHNICAL_VISIT -> {
                TechnicalVisitDocument tv = new TechnicalVisitDocument();
                tv.setInspectionCenter(request.documentNumber());
                yield tv;
            }
        };

        doc.setCompanyId(companyId);
        doc.setVehicle(vehicle);
        doc.setIssueDate(request.issueDate());
        doc.setExpirationDate(request.expirationDate());

        return mapDocToDTO(vehicleDocumentRepository.save(doc));
    }

    // ---- Maintenance Toggle ----

    @Transactional
    public VehicleResponseDTO toggleMaintenanceStatus(UUID vehicleId, boolean underMaintenance) {
        UUID companyId = getCurrentCompanyId();
        Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));
        vehicle.setUnderMaintenance(underMaintenance);
        return mapToResponseDTO(vehicleRepository.save(vehicle));
    }

    /**
     * Package-accessible method used by MissionService to resolve a vehicle
     * by ID within the correct tenant scope.
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

    private DocumentResponseDTO mapDocToDTO(VehicleDocument doc) {
        return new DocumentResponseDTO(
                doc.getId(),
                doc.getDocumentType() != null ? doc.getDocumentType().name() : doc.getClass().getSimpleName(),
                doc.getDocumentReference(),
                doc.getIssueDate(),
                doc.getExpirationDate()
        );
    }
}

