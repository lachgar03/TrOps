package com.TrOps.mvp.vehicle.service;

import com.TrOps.mvp.common.exception.ResourceNotFoundException;
import com.TrOps.mvp.mission.dto.MissionSummaryDTO;
import com.TrOps.mvp.mission.service.MissionQueryService;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.vehicle.dto.VehicleFinancialSummaryDTO;
import com.TrOps.mvp.vehicle.model.Vehicle;
import com.TrOps.mvp.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private MissionQueryService missionQueryService;

    @InjectMocks
    private VehicleService vehicleService;

    private UUID companyId;
    private UUID vehicleId;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        vehicleId = UUID.randomUUID();

        vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setCompanyId(companyId);
        vehicle.setRegistrationNumber("TEST-123");

        // Mock Security Context for companyId resolution
        User mockUser = new User();
        mockUser.setCompanyId(companyId);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication())
                .thenReturn(new UsernamePasswordAuthenticationToken(mockUser, null));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldAggregateFinancialSummaryForMultipleMissions() {
        // Given
        MissionSummaryDTO m1 = new MissionSummaryDTO(
                UUID.randomUUID(), vehicleId, UUID.randomUUID(),
                new BigDecimal("1000.00"), new BigDecimal("400.00"), new BigDecimal("600.00"), "COMPLETED");

        MissionSummaryDTO m2 = new MissionSummaryDTO(
                UUID.randomUUID(), vehicleId, UUID.randomUUID(),
                new BigDecimal("500.00"), new BigDecimal("550.00"), new BigDecimal("-50.00"), "COMPLETED");

        when(vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)).thenReturn(Optional.of(vehicle));
        when(missionQueryService.getMissionsByVehicle(vehicleId, companyId)).thenReturn(List.of(m1, m2));

        // When
        VehicleFinancialSummaryDTO summary = vehicleService.getVehicleFinancialSummary(vehicleId);

        // Then
        assertThat(summary.totalRevenues()).isEqualByComparingTo("1500.00");
        assertThat(summary.totalCosts()).isEqualByComparingTo("950.00");
        assertThat(summary.totalProfit()).isEqualByComparingTo("550.00");
        assertThat(summary.registrationNumber()).isEqualTo("TEST-123");
    }

    @Test
    void shouldReturnZeroSummaryWhenNoMissionsExist() {
        // Given
        when(vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)).thenReturn(Optional.of(vehicle));
        when(missionQueryService.getMissionsByVehicle(vehicleId, companyId)).thenReturn(Collections.emptyList());

        // When
        VehicleFinancialSummaryDTO summary = vehicleService.getVehicleFinancialSummary(vehicleId);

        // Then
        assertThat(summary.totalRevenues()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.totalCosts()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.totalProfit()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldThrowExceptionWhenVehicleNotFound() {
        // Given
        when(vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vehicleService.getVehicleFinancialSummary(vehicleId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("introuvable");
    }
}

