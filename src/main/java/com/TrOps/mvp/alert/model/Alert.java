package com.TrOps.mvp.alert.model;

import com.TrOps.mvp.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "alerts")
@Getter
@Setter
public class Alert extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertLevel level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status = AlertStatus.ACTIVE;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Optional foreign keys based on the alert context
    // We store the ID directly rather than a ManyToOne to avoid hard coupling
    // across all modules (Mission, Vehicle, Client) into the Alert module
    @Column(name = "reference_id")
    private String referenceId;
}
