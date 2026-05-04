package com.TrOps.mvp.alert.service;

import com.TrOps.mvp.alert.dto.AlertResponseDTO;
import com.TrOps.mvp.alert.model.Alert;
import com.TrOps.mvp.alert.model.AlertLevel;
import com.TrOps.mvp.alert.model.AlertStatus;
import com.TrOps.mvp.alert.model.AlertType;
import com.TrOps.mvp.alert.repository.AlertRepository;
import com.TrOps.mvp.common.exception.ResourceNotFoundException;
import com.TrOps.mvp.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional
    public void createAlert(UUID companyId, AlertType type, AlertLevel level, String title, String description, String referenceId) {
        Alert alert = new Alert();
        alert.setCompanyId(companyId);
        alert.setType(type);
        alert.setLevel(level);
        alert.setTitle(title);
        alert.setDescription(description);
        alert.setReferenceId(referenceId);
        alert.setStatus(AlertStatus.ACTIVE);

        alertRepository.save(alert);
    }

    public List<AlertResponseDTO> getActiveAlerts() {
        UUID companyId = getCurrentCompanyId();
        return alertRepository.findAllByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, AlertStatus.ACTIVE)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void resolveAlert(UUID alertId) {
        UUID companyId = getCurrentCompanyId();
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alerte introuvable"));

        if (!alert.getCompanyId().equals(companyId)) {
            throw new ResourceNotFoundException("Alerte introuvable pour cette entreprise");
        }

        alert.setStatus(AlertStatus.RESOLVED);
        alertRepository.save(alert);
    }

    private UUID getCurrentCompanyId() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getCompanyId();
    }

    private AlertResponseDTO mapToDTO(Alert alert) {
        return new AlertResponseDTO(
                alert.getId(),
                alert.getType().name(),
                alert.getLevel().name(),
                alert.getStatus().name(),
                alert.getTitle(),
                alert.getDescription(),
                alert.getReferenceId(),
                alert.getCreatedAt()
        );
    }
}
