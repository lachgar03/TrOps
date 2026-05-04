package com.TrOps.mvp.mission.repository;

import com.TrOps.mvp.mission.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissionRepository extends JpaRepository<Mission, UUID> {
    Optional<Mission> findByIdAndCompanyId(UUID id, UUID companyId);
}
