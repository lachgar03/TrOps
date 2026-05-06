package com.TrOps.mvp.vehicle.model;

import com.TrOps.mvp.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "vehicles",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_vehicle_registration_company",
                columnNames = {"registration_number", "company_id"}
        )
)
@Getter
@Setter
public class Vehicle extends BaseEntity {

    @Column(name = "registration_number", nullable = false)
    private String registrationNumber;

    @Column(nullable = false, columnDefinition = "varchar(255) default 'Unknown'")
    private String brand;

    @Column(nullable = false, columnDefinition = "varchar(255) default 'Unknown'")
    private String model;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer currentMileage;

    @Column(nullable = false)
    private boolean isUnderMaintenance = false;
}

