package com.TrOps.mvp.mission.repository;

import com.TrOps.mvp.mission.model.Mission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissionRepository extends JpaRepository<Mission, UUID> {
    
    @EntityGraph(attributePaths = {"vehicle", "client"})
    Optional<Mission> findByIdAndCompanyId(UUID id, UUID companyId);

    @EntityGraph(attributePaths = {"vehicle", "client"})
    List<Mission> findAllByCompanyId(UUID companyId);

    List<Mission> findAllByVehicleIdAndCompanyId(UUID vehicleId, UUID companyId);
}
