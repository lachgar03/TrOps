package com.TrOps.mvp.vehicle.model;

import com.TrOps.mvp.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
public class Vehicle extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @Column(nullable = false)
    private boolean isUnderMaintenance = false;
}
