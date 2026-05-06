package com.TrOps.mvp.alert.repository;

import com.TrOps.mvp.alert.model.Alert;
import com.TrOps.mvp.alert.model.AlertStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    Optional<Alert> findByIdAndCompanyId(UUID id, UUID companyId);
    Page<Alert> findAllByCompanyIdAndStatusOrderByCreatedAtDesc(UUID companyId, AlertStatus status, Pageable pageable);
}

