package com.TrOps.mvp.vehicle.service;

import com.TrOps.mvp.alert.model.AlertLevel;
import com.TrOps.mvp.alert.model.AlertType;
import com.TrOps.mvp.alert.service.AlertService;
import com.TrOps.mvp.vehicle.model.DocumentType;
import com.TrOps.mvp.vehicle.model.InsuranceDocument;
import com.TrOps.mvp.vehicle.model.Vehicle;
import com.TrOps.mvp.vehicle.repository.VehicleDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentAlertServiceTest {

    @Mock
    private VehicleDocumentRepository vehicleDocumentRepository;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private DocumentAlertService documentAlertService;

    private UUID companyId;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        vehicle = new Vehicle();
        vehicle.setRegistrationNumber("TEST-123");
    }

    @Test
    void shouldCreateHighAlertForExpiredDocument() {
        // Given
        InsuranceDocument expiredDoc = new InsuranceDocument();
        expiredDoc.setId(UUID.randomUUID());
        expiredDoc.setCompanyId(companyId);
        expiredDoc.setVehicle(vehicle);
        expiredDoc.setPolicyNumber("POL-123");
        expiredDoc.setExpirationDate(LocalDate.now().minusDays(2)); // Expired 2 days ago
        org.springframework.test.util.ReflectionTestUtils.setField(expiredDoc, "documentType", DocumentType.INSURANCE);

        when(vehicleDocumentRepository.findAllByExpirationDateBefore(any(LocalDate.class)))
                .thenReturn(List.of(expiredDoc));

        // When
        documentAlertService.checkExpiringDocuments();

        // Then
        verify(alertService).createAlert(
                eq(companyId),
                eq(AlertType.DOCUMENT_EXPIRATION),
                eq(AlertLevel.HIGH),
                anyString(),
                anyString(),
                eq(expiredDoc.getId().toString())
        );
    }

    @Test
    void shouldCreateMediumAlertForDocumentExpiringSoon() {
        // Given
        InsuranceDocument soonDoc = new InsuranceDocument();
        soonDoc.setId(UUID.randomUUID());
        soonDoc.setCompanyId(companyId);
        soonDoc.setVehicle(vehicle);
        soonDoc.setPolicyNumber("POL-456");
        soonDoc.setExpirationDate(LocalDate.now().plusDays(10)); // Expires in 10 days
        org.springframework.test.util.ReflectionTestUtils.setField(soonDoc, "documentType", DocumentType.INSURANCE);

        when(vehicleDocumentRepository.findAllByExpirationDateBefore(any(LocalDate.class)))
                .thenReturn(List.of(soonDoc));

        // When
        documentAlertService.checkExpiringDocuments();

        // Then
        verify(alertService).createAlert(
                eq(companyId),
                eq(AlertType.DOCUMENT_EXPIRATION),
                eq(AlertLevel.MEDIUM),
                anyString(),
                anyString(),
                eq(soonDoc.getId().toString())
        );
    }

    @Test
    void shouldCreateLowAlertForDocumentExpiringLater() {
        // Given
        InsuranceDocument laterDoc = new InsuranceDocument();
        laterDoc.setId(UUID.randomUUID());
        laterDoc.setCompanyId(companyId);
        laterDoc.setVehicle(vehicle);
        laterDoc.setPolicyNumber("POL-789");
        laterDoc.setExpirationDate(LocalDate.now().plusDays(25)); // Expires in 25 days
        org.springframework.test.util.ReflectionTestUtils.setField(laterDoc, "documentType", DocumentType.INSURANCE);

        when(vehicleDocumentRepository.findAllByExpirationDateBefore(any(LocalDate.class)))
                .thenReturn(List.of(laterDoc));

        // When
        documentAlertService.checkExpiringDocuments();

        // Then
        verify(alertService).createAlert(
                eq(companyId),
                eq(AlertType.DOCUMENT_EXPIRATION),
                eq(AlertLevel.LOW),
                anyString(),
                anyString(),
                eq(laterDoc.getId().toString())
        );
    }
}
