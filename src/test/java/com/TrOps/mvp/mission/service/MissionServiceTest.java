package com.TrOps.mvp.mission.service;

import com.TrOps.mvp.client.model.Client;
import com.TrOps.mvp.client.service.ClientService;
import com.TrOps.mvp.common.exception.BusinessRuleException;
import com.TrOps.mvp.mission.dto.MissionRequestDTO;
import com.TrOps.mvp.mission.dto.MissionResponseDTO;
import com.TrOps.mvp.mission.model.Mission;
import com.TrOps.mvp.mission.model.ProfitabilityScore;
import com.TrOps.mvp.mission.repository.MissionRepository;
import com.TrOps.mvp.mission.service.strategy.ProfitabilityResult;
import com.TrOps.mvp.mission.service.strategy.ProfitabilityStrategy;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.vehicle.model.Vehicle;
import com.TrOps.mvp.vehicle.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private ClientService clientService;

    @Mock
    private ProfitabilityStrategy profitabilityStrategy;

    @InjectMocks
    private MissionService missionService;

    private final UUID companyId = UUID.randomUUID();
    private final UUID vehicleId = UUID.randomUUID();
    private final UUID clientId  = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication  = mock(Authentication.class);
        User mockUser = new User();
        mockUser.setCompanyId(companyId);

        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(mockUser);
    }

    @Test
    void devrraitCalculerLeProfitCorrectementLorsDeLaCreation() {
        // Arrange
        MissionRequestDTO request = new MissionRequestDTO(
                vehicleId, clientId,
                new BigDecimal("1000.00"),
                new BigDecimal("300.00")
        );

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setRegistrationNumber("AB-123-CD");
        vehicle.setUnderMaintenance(false);

        Client client = new Client();
        client.setId(clientId);
        client.setName("Client Test");

        // Mock service calls (no repository access)
        when(vehicleService.resolveVehicle(vehicleId, companyId)).thenReturn(vehicle);
        when(clientService.resolveClient(clientId, companyId)).thenReturn(client);

        when(profitabilityStrategy.calculate(new BigDecimal("1000.00"), new BigDecimal("300.00")))
                .thenReturn(new ProfitabilityResult(
                        new BigDecimal("700.00"),
                        new BigDecimal("0.70"),
                        ProfitabilityScore.PROFITABLE));

        when(missionRepository.saveAndFlush(any(Mission.class))).thenAnswer(invocation -> {
            Mission m = invocation.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        // Act
        MissionResponseDTO response = missionService.createMission(request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("700.00"), response.profit());
        verify(missionRepository, times(1)).saveAndFlush(any(Mission.class));
    }

    @Test
    void devraitLancerUneExceptionSiLeVehiculeEstEnMaintenance() {
        // Arrange
        MissionRequestDTO request = new MissionRequestDTO(
                vehicleId, clientId,
                new BigDecimal("1000.00"),
                new BigDecimal("300.00")
        );

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setUnderMaintenance(true);

        when(vehicleService.resolveVehicle(vehicleId, companyId)).thenReturn(vehicle);

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> missionService.createMission(request));

        assertEquals("Impossible d'assigner la mission : Le véhicule est en maintenance",
                exception.getMessage());
        verify(missionRepository, never()).save(any(Mission.class));
    }
}

