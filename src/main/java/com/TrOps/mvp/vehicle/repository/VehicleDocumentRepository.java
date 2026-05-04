package com.TrOps.mvp.vehicle.repository;

import com.TrOps.mvp.vehicle.model.VehicleDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleDocumentRepository extends JpaRepository<VehicleDocument, UUID> {
    List<VehicleDocument> findAllByVehicleIdAndCompanyId(UUID vehicleId, UUID companyId);
    List<VehicleDocument> findAllByExpirationDateBefore(LocalDate date);
}
