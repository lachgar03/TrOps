package com.TrOps.mvp.vehicle.controller;

import com.TrOps.mvp.vehicle.dto.DocumentRequestDTO;
import com.TrOps.mvp.vehicle.dto.DocumentResponseDTO;
import com.TrOps.mvp.vehicle.dto.VehicleFinancialSummaryDTO;
import com.TrOps.mvp.vehicle.dto.VehicleRequestDTO;
import com.TrOps.mvp.vehicle.dto.VehicleResponseDTO;
import com.TrOps.mvp.vehicle.service.VehicleService;
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
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Gestion de la flotte de véhicules")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @Operation(summary = "Ajouter un véhicule à la flotte")
    public ResponseEntity<VehicleResponseDTO> createVehicle(@Valid @RequestBody VehicleRequestDTO request) {
        VehicleResponseDTO response = vehicleService.createVehicle(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les véhicules (paginé)")
    public ResponseEntity<Page<VehicleResponseDTO>> getAllVehicles(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(vehicleService.getAllVehicles(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un véhicule par ID")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/{id}/financial-summary")
    @Operation(summary = "Résumé financier d'un véhicule (agrégation de ses missions)")
    public ResponseEntity<VehicleFinancialSummaryDTO> getVehicleFinancialSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.getVehicleFinancialSummary(id));
    }

    @GetMapping("/{id}/documents")
    @Operation(summary = "Lister les documents d'un véhicule")
    public ResponseEntity<java.util.List<DocumentResponseDTO>> getVehicleDocuments(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.getDocumentsByVehicle(id));
    }

    @PostMapping("/{id}/documents")
    @Operation(summary = "Ajouter un document à un véhicule")
    public ResponseEntity<DocumentResponseDTO> addDocument(
            @PathVariable UUID id,
            @Valid @RequestBody DocumentRequestDTO request) {
        return ResponseEntity.status(201).body(vehicleService.addDocument(id, request));
    }

    @PutMapping("/{vehicleId}/maintenance-status")
    @Operation(summary = "Basculer le statut de maintenance d'un véhicule")
    public ResponseEntity<VehicleResponseDTO> toggleMaintenance(
            @PathVariable UUID vehicleId,
            @RequestParam boolean underMaintenance) {
        return ResponseEntity.ok(vehicleService.toggleMaintenanceStatus(vehicleId, underMaintenance));
    }
}

