package com.TrOps.mvp.maintenance.controller;

import com.TrOps.mvp.maintenance.dto.MaintenanceRequestDTO;
import com.TrOps.mvp.maintenance.dto.MaintenanceResponseDTO;
import com.TrOps.mvp.maintenance.dto.MaintenanceStatusUpdateDTO;
import com.TrOps.mvp.maintenance.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/maintenance")
@RequiredArgsConstructor
@Tag(name = "Maintenance", description = "Gestion de la maintenance des véhicules")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    @Operation(summary = "Planifier une nouvelle maintenance")
    public ResponseEntity<MaintenanceResponseDTO> createMaintenance(
            @Valid @RequestBody MaintenanceRequestDTO request) {
        MaintenanceResponseDTO response = maintenanceService.createMaintenance(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les maintenances (paginé)")
    public ResponseEntity<Page<MaintenanceResponseDTO>> getAllMaintenanceLogs(
            @RequestParam(required = false) UUID vehicleId,
            @PageableDefault(size = 20, sort = "maintenanceDate", direction = Sort.Direction.DESC) Pageable pageable) {
        if (vehicleId != null) {
            return ResponseEntity.ok(maintenanceService.getByVehicle(vehicleId, pageable));
        }
        return ResponseEntity.ok(maintenanceService.getAllMaintenanceLogs(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une maintenance par ID")
    public ResponseEntity<MaintenanceResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(maintenanceService.getById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une maintenance")
    public ResponseEntity<MaintenanceResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody MaintenanceStatusUpdateDTO request) {
        return ResponseEntity.ok(maintenanceService.updateStatus(id, request));
    }
}
