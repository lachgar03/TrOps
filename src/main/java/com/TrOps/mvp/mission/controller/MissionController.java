package com.TrOps.mvp.mission.controller;

import com.TrOps.mvp.mission.dto.MissionRequestDTO;
import com.TrOps.mvp.mission.dto.MissionResponseDTO;
import com.TrOps.mvp.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @PostMapping
    public ResponseEntity<MissionResponseDTO> createMission(@RequestBody MissionRequestDTO request) {
        MissionResponseDTO response = missionService.createMission(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
