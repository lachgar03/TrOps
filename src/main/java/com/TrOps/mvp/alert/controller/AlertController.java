package com.TrOps.mvp.alert.controller;

import com.TrOps.mvp.alert.dto.AlertResponseDTO;
import com.TrOps.mvp.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/active")
    public ResponseEntity<List<AlertResponseDTO>> getActiveAlerts() {
        return ResponseEntity.ok(alertService.getActiveAlerts());
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveAlert(@PathVariable UUID id) {
        alertService.resolveAlert(id);
        return ResponseEntity.ok().build();
    }
}
