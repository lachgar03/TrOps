package com.TrOps.mvp.mission.model;

import com.TrOps.mvp.client.model.Client;
import com.TrOps.mvp.common.model.BaseEntity;
import com.TrOps.mvp.vehicle.model.Vehicle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "missions")
@Getter
@Setter
public class Mission extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    private BigDecimal revenues;

    @Column(nullable = false)
    private BigDecimal costs;

    @Column(nullable = false)
    private BigDecimal profit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'PLANNED'")
    private MissionStatus status = MissionStatus.PLANNED;

    @Enumerated(EnumType.STRING)
    @Column(name = "profitability_score")
    private ProfitabilityScore profitabilityScore;

    @Column(name = "profit_margin", precision = 10, scale = 4)
    private BigDecimal profitMargin;
}
