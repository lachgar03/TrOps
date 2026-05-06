package com.TrOps.mvp.vehicle.repository;

import com.TrOps.mvp.vehicle.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByIdAndCompanyId(UUID id, UUID companyId);
    Page<Vehicle> findAllByCompanyId(UUID companyId, Pageable pageable);
}

