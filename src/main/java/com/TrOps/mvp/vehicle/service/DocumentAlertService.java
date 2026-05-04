package com.TrOps.mvp.vehicle.service;

import com.TrOps.mvp.alert.model.AlertLevel;
import com.TrOps.mvp.alert.model.AlertType;
import com.TrOps.mvp.alert.service.AlertService;
import com.TrOps.mvp.vehicle.model.VehicleDocument;
import com.TrOps.mvp.vehicle.repository.VehicleDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentAlertService {

    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final AlertService alertService;

    // Runs every day at 1 AM
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void checkExpiringDocuments() {
        log.info("Démarrage du job de vérification des documents expirant...");
        
        LocalDate thresholdDate = LocalDate.now().plusDays(30);
        List<VehicleDocument> expiringDocs = vehicleDocumentRepository.findAllByExpirationDateBefore(thresholdDate);

        for (VehicleDocument doc : expiringDocs) {
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), doc.getExpirationDate());
            AlertLevel level = daysRemaining <= 0 ? AlertLevel.HIGH : (daysRemaining <= 15 ? AlertLevel.MEDIUM : AlertLevel.LOW);
            
            String title = "Expiration Document: " + doc.getDocumentType().name();
            String description = String.format("Le document %s (Réf: %s) pour le véhicule %s expire le %s.",
                    doc.getDocumentType().name(),
                    doc.getDocumentReference(),
                    doc.getVehicle().getRegistrationNumber(),
                    doc.getExpirationDate().toString()
            );

            // TODO: Ensure we don't create duplicate alerts if one is already active for this document
            // For MVP we just create the alert
            log.warn("Génération d'alerte: {} ({} jours restants)", title, daysRemaining);
            
            alertService.createAlert(
                    doc.getCompanyId(),
                    AlertType.DOCUMENT_EXPIRATION,
                    level,
                    title,
                    description,
                    doc.getId().toString()
            );
        }

        log.info("Fin du job de vérification des documents. {} documents traités.", expiringDocs.size());
    }
}
