package com.TrOps.mvp.mission.controller;

import com.TrOps.mvp.mission.dto.MissionRequestDTO;
import com.TrOps.mvp.mission.dto.MissionResponseDTO;
import com.TrOps.mvp.mission.dto.MissionStatusUpdateDTO;
import com.TrOps.mvp.mission.model.MissionStatus;
import com.TrOps.mvp.mission.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/missions")
@RequiredArgsConstructor
@Tag(name = "Missions", description = "Gestion des missions de transport")
public class MissionController {

    private final MissionService missionService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle mission")
    public ResponseEntity<MissionResponseDTO> createMission(@Valid @RequestBody MissionRequestDTO request) {
        MissionResponseDTO response = missionService.createMission(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les missions (paginé, filtrable par statut)")
    public ResponseEntity<Page<MissionResponseDTO>> getAllMissions(
            @RequestParam(required = false) MissionStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (status != null) {
            return ResponseEntity.ok(missionService.getMissionsByStatus(status, pageable));
        }
        return ResponseEntity.ok(missionService.getAllMissions(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une mission par ID")
    public ResponseEntity<MissionResponseDTO> getMissionById(@PathVariable UUID id) {
        return ResponseEntity.ok(missionService.getMissionById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une mission (transitions validées)")
    public ResponseEntity<MissionResponseDTO> updateMissionStatus(
            @PathVariable UUID id,
            @Valid @RequestBody MissionStatusUpdateDTO request) {
        return ResponseEntity.ok(missionService.updateStatus(id, request));
    }
}

