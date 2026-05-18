package com.TrOps.mvp.mission.service;

import com.TrOps.mvp.client.model.Client;
import com.TrOps.mvp.client.service.ClientService;
import com.TrOps.mvp.common.exception.BusinessRuleException;
import com.TrOps.mvp.mission.dto.MissionRequestDTO;
import com.TrOps.mvp.mission.dto.MissionResponseDTO;
import com.TrOps.mvp.mission.model.Mission;
import com.TrOps.mvp.mission.model.MissionStatus;
import com.TrOps.mvp.mission.model.ProfitabilityScore;
import com.TrOps.mvp.mission.repository.MissionRepository;
import com.TrOps.mvp.mission.service.strategy.ProfitabilityResult;
import com.TrOps.mvp.mission.service.strategy.ProfitabilityStrategy;
import com.TrOps.mvp.user.model.Role;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.vehicle.model.Vehicle;
import com.TrOps.mvp.vehicle.repository.VehicleRepository;
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
public class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private ProfitabilityStrategy profitabilityStrategy;

    @InjectMocks
    private MissionService missionService;

    private UUID companyId;
    private UUID vehicleId;
    private UUID clientId;
    private User currentUser;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        vehicleId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        // Mock SecurityContext
        currentUser = new User();
        currentUser.setId(UUID.randomUUID());
        currentUser.setCompanyId(companyId);
        currentUser.setEmail("test@ops.com");
        currentUser.setRole(Role.MANAGER);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(currentUser);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCalculateProfitCorrectlyWhenRevenuesAndCostsAreProvided() {
        // Arrange
        BigDecimal revenues = new BigDecimal("1500.00");
        BigDecimal costs = new BigDecimal("1000.00");
        BigDecimal expectedProfit = revenues.subtract(costs); // 500.00

        MissionRequestDTO request = new MissionRequestDTO(vehicleId, clientId, revenues, costs);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setCompanyId(companyId);
        vehicle.setUnderMaintenance(false);
        vehicle.setRegistrationNumber("AB-123-CD");

        Client client = new Client();
        client.setId(clientId);
        client.setCompanyId(companyId);
        client.setName("Client Test");

        when(vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)).thenReturn(Optional.of(vehicle));
        when(clientService.resolveClient(clientId, companyId)).thenReturn(client);
        when(profitabilityStrategy.calculate(revenues, costs))
                .thenReturn(new ProfitabilityResult(expectedProfit, new BigDecimal("0.33"), ProfitabilityScore.PROFITABLE));
        
        when(missionRepository.saveAndFlush(any(Mission.class))).thenAnswer(invocation -> {
            Mission savedMission = invocation.getArgument(0);
            savedMission.setId(UUID.randomUUID());
            savedMission.setStatus(MissionStatus.PLANNED);
            return savedMission;
        });

        // Act
        MissionResponseDTO response = missionService.createMission(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedProfit, response.profit());
        verify(profitabilityStrategy, times(1)).calculate(revenues, costs);
        verify(missionRepository, times(1)).saveAndFlush(any(Mission.class));
    }

    @Test
    void shouldThrowBusinessRuleExceptionWhenVehicleIsUnderMaintenance() {
        // Arrange
        BigDecimal revenues = new BigDecimal("1500.00");
        BigDecimal costs = new BigDecimal("1000.00");
        
        MissionRequestDTO request = new MissionRequestDTO(vehicleId, clientId, revenues, costs);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setCompanyId(companyId);
        vehicle.setUnderMaintenance(true); // Véhicule en maintenance !

        when(vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)).thenReturn(Optional.of(vehicle));

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            missionService.createMission(request);
        });

        assertEquals("Impossible d'assigner la mission : Le véhicule est en maintenance", exception.getMessage());
        
        // Vérifier que la mission n'a pas été sauvegardée et que le client n'a pas été cherché
        verify(clientService, never()).resolveClient(any(), any());
        verify(missionRepository, never()).saveAndFlush(any());
    }
}
