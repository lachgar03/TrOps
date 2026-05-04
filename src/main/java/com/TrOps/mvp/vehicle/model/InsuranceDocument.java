package com.TrOps.mvp.vehicle.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("INSURANCE")
@Getter
@Setter
public class InsuranceDocument extends VehicleDocument {

    @Column(name = "policy_number")
    private String policyNumber;

    @Column(name = "provider_name")
    private String providerName;

    @Override
    @Transient
    public String getDocumentReference() {
        return policyNumber;
    }
}
