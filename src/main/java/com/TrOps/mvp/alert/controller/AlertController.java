package com.TrOps.mvp.alert.controller;

import com.TrOps.mvp.alert.dto.AlertResponseDTO;
import com.TrOps.mvp.alert.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "Système d'alertes intelligentes")
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/active")
    @Operation(summary = "Lister les alertes actives (paginé)")
    public ResponseEntity<Page<AlertResponseDTO>> getActiveAlerts(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(alertService.getActiveAlerts(pageable));
    }

    @PostMapping("/{id}/resolve")
    @Operation(summary = "Résoudre une alerte")
    public ResponseEntity<Void> resolveAlert(@PathVariable UUID id) {
        alertService.resolveAlert(id);
        return ResponseEntity.ok().build();
    }
}

