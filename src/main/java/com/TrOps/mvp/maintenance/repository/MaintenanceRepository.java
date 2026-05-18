package com.TrOps.mvp.maintenance.repository;

import com.TrOps.mvp.vehicle.model.MaintenanceLog;
import com.TrOps.mvp.vehicle.model.MaintenanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceLog, UUID> {
    Optional<MaintenanceLog> findByIdAndCompanyId(UUID id, UUID companyId);
    Page<MaintenanceLog> findAllByCompanyId(UUID companyId, Pageable pageable);
    Page<MaintenanceLog> findAllByCompanyIdAndVehicleId(UUID companyId, UUID vehicleId, Pageable pageable);
    Page<MaintenanceLog> findAllByCompanyIdAndStatus(UUID companyId, MaintenanceStatus status, Pageable pageable);
    long countByCompanyIdAndStatus(UUID companyId, MaintenanceStatus status);
}
