package com.TrOps.mvp.vehicle.controller;

import com.TrOps.mvp.vehicle.dto.VehicleFinancialSummaryDTO;
import com.TrOps.mvp.vehicle.dto.VehicleRequestDTO;
import com.TrOps.mvp.vehicle.dto.VehicleResponseDTO;
import com.TrOps.mvp.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponseDTO> createVehicle(@Valid @RequestBody VehicleRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.createVehicle(request));
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/{id}/financial-summary")
    public ResponseEntity<VehicleFinancialSummaryDTO> getVehicleFinancialSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.getVehicleFinancialSummary(id));
    }
}
