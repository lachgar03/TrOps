package com.TrOps.mvp.vehicle.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("REGISTRATION")
@Getter
@Setter
public class RegistrationDocument extends VehicleDocument {

    @Column(name = "chassis_number")
    private String chassisNumber;

    @Column(name = "empty_weight")
    private Integer emptyWeight;

    @Override
    @Transient
    public String getDocumentReference() {
        return chassisNumber;
    }
}
