package com.TrOps.mvp.mission.service;

import com.TrOps.mvp.client.model.Client;
import com.TrOps.mvp.client.repository.ClientRepository;
import com.TrOps.mvp.mission.dto.MissionRequestDTO;
import com.TrOps.mvp.mission.dto.MissionResponseDTO;
import com.TrOps.mvp.mission.model.Mission;
import com.TrOps.mvp.mission.repository.MissionRepository;
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

import java.lang.reflect.Method;
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
    private VehicleRepository vehicleRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private MissionService missionService;

    private final UUID companyId = UUID.randomUUID();
    private final UUID vehicleId = UUID.randomUUID();
    private final UUID clientId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Simuler le contexte de sécurité Spring pour récupérer le companyId
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User mockUser = new User();
        mockUser.setCompanyId(companyId);

        // Configuration du contexte uniquement pour ce test
        SecurityContextHolder.setContext(securityContext);
        
        // Lenient car certaines méthodes n'en auront pas besoin (ex: quand ça échoue avant d'appeler l'utilisateur)
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(mockUser);
    }

    @Test
    void devrraitCalculerLeProfitCorrectementLorsDeLaCreation() throws Exception {
        // Arrange
        MissionRequestDTO request = new MissionRequestDTO(
                vehicleId,
                clientId,
                new BigDecimal("1000.00"), // Revenus
                new BigDecimal("300.00")   // Coûts
        );

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setRegistrationNumber("AB-123-CD");
        vehicle.setUnderMaintenance(false); // Véhicule disponible

        Client client = new Client();
        client.setId(clientId);
        client.setName("Client Test");

        when(vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)).thenReturn(Optional.of(vehicle));
        when(clientRepository.findByIdAndCompanyId(clientId, companyId)).thenReturn(Optional.of(client));

        // Simuler le comportement de JPA (@PrePersist) lors du save
        when(missionRepository.save(any(Mission.class))).thenAnswer(invocation -> {
            Mission mission = invocation.getArgument(0);
            
            // Appel de la méthode privée calculateProfit via réflexion
            Method calculateProfitMethod = Mission.class.getDeclaredMethod("calculateProfit");
            calculateProfitMethod.setAccessible(true);
            calculateProfitMethod.invoke(mission);
            
            mission.setId(UUID.randomUUID());
            return mission;
        });

        // Act
        MissionResponseDTO response = missionService.createMission(request);

        // Assert
        assertNotNull(response);
        // Profit attendu : 1000 - 300 = 700
        assertEquals(new BigDecimal("700.00"), response.profit());
        verify(missionRepository, times(1)).save(any(Mission.class));
    }

    @Test
    void devraitLancerUneExceptionSiLeVehiculeEstEnMaintenance() {
        // Arrange
        MissionRequestDTO request = new MissionRequestDTO(
                vehicleId,
                clientId,
                new BigDecimal("1000.00"),
                new BigDecimal("300.00")
        );

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setUnderMaintenance(true); // Véhicule EN MAINTENANCE

        when(vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)).thenReturn(Optional.of(vehicle));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            missionService.createMission(request);
        });

        assertEquals("Impossible d'assigner la mission : Le véhicule est en maintenance", exception.getMessage());
        
        // Vérifier que le repository n'a jamais été appelé pour sauvegarder la mission
        verify(missionRepository, never()).save(any(Mission.class));
    }
}
