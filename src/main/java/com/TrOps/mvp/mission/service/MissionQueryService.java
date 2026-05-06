package com.TrOps.mvp.mission.service;

import com.TrOps.mvp.mission.dto.MissionSummaryDTO;

import java.util.List;
import java.util.UUID;

/**
 * Read-only interface exposing mission data to other modules.
 * This breaks the circular dependency between VehicleService and MissionService
 * without creating cross-module repository access.
 */
public interface MissionQueryService {

    List<MissionSummaryDTO> getMissionsByVehicle(UUID vehicleId, UUID companyId);

    List<MissionSummaryDTO> getMissionsByClient(UUID clientId, UUID companyId);
}
