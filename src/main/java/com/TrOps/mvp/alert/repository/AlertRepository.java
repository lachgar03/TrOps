package com.TrOps.mvp.alert.repository;

import com.TrOps.mvp.alert.model.Alert;
import com.TrOps.mvp.alert.model.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findAllByCompanyIdOrderByCreatedAtDesc(UUID companyId);
    List<Alert> findAllByCompanyIdAndStatusOrderByCreatedAtDesc(UUID companyId, AlertStatus status);
}
