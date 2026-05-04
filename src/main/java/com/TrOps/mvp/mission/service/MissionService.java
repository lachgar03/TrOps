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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public MissionResponseDTO createMission(MissionRequestDTO request) {
        UUID companyId = getCurrentCompanyId();

        // 1. Vérification du Véhicule (Appartenance + Disponibilité)
        Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(request.vehicleId(), companyId)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule introuvable pour cette entreprise"));

        if (vehicle.isUnderMaintenance()) {
            throw new IllegalArgumentException("Impossible d'assigner la mission : Le véhicule est en maintenance");
        }

        // 2. Vérification du Client
        Client client = clientRepository.findByIdAndCompanyId(request.clientId(), companyId)
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable pour cette entreprise"));

        // 3. Création de la Mission
        Mission mission = new Mission();
        mission.setCompanyId(companyId);
        mission.setVehicle(vehicle);
        mission.setClient(client);
        mission.setRevenues(request.revenues());
        mission.setCosts(request.costs());
        
        // Le calcul du profit se fait automatiquement via @PrePersist dans l'entité Mission

        Mission savedMission = missionRepository.saveAndFlush(mission);

        return mapToResponseDTO(savedMission);
    }

    private UUID getCurrentCompanyId() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getCompanyId();
    }

    private MissionResponseDTO mapToResponseDTO(Mission mission) {
        return new MissionResponseDTO(
                mission.getId(),
                mission.getVehicle().getId(),
                mission.getVehicle().getRegistrationNumber(),
                mission.getClient().getId(),
                mission.getClient().getName(),
                mission.getRevenues(),
                mission.getCosts(),
                mission.getProfit(),
                mission.getCreatedAt()
        );
    }
}
