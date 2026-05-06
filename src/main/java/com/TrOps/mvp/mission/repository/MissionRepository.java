package com.TrOps.mvp.mission.repository;

import com.TrOps.mvp.mission.model.Mission;
import com.TrOps.mvp.mission.model.MissionStatus;
import com.TrOps.mvp.mission.model.ProfitabilityScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissionRepository extends JpaRepository<Mission, UUID> {

    @EntityGraph(attributePaths = {"vehicle", "client"})
    Optional<Mission> findByIdAndCompanyId(UUID id, UUID companyId);

    @EntityGraph(attributePaths = {"vehicle", "client"})
    Page<Mission> findAllByCompanyId(UUID companyId, Pageable pageable);

    @EntityGraph(attributePaths = {"vehicle", "client"})
    Page<Mission> findAllByCompanyIdAndStatus(UUID companyId, MissionStatus status, Pageable pageable);

    // Used by VehicleService via MissionQueryService — no direct cross-module repo call
    List<Mission> findAllByVehicleIdAndCompanyId(UUID vehicleId, UUID companyId);

    List<Mission> findAllByClientIdAndCompanyId(UUID clientId, UUID companyId);

    // ---- Dashboard aggregation queries ----

    @Query("SELECT COALESCE(SUM(m.revenues), 0) FROM Mission m WHERE m.companyId = :companyId")
    BigDecimal sumRevenuesByCompanyId(@Param("companyId") UUID companyId);

    @Query("SELECT COALESCE(SUM(m.costs), 0) FROM Mission m WHERE m.companyId = :companyId")
    BigDecimal sumCostsByCompanyId(@Param("companyId") UUID companyId);

    @Query("SELECT COALESCE(SUM(m.profit), 0) FROM Mission m WHERE m.companyId = :companyId")
    BigDecimal sumProfitByCompanyId(@Param("companyId") UUID companyId);

    long countByCompanyId(UUID companyId);

    long countByCompanyIdAndProfitabilityScore(UUID companyId, ProfitabilityScore score);

    @Query("""
            SELECT m FROM Mission m
            WHERE m.companyId = :companyId AND m.profitabilityScore = 'LOSS'
            ORDER BY m.profit ASC
            """)
    List<Mission> findMissionsAtLoss(@Param("companyId") UUID companyId, Pageable pageable);
}

