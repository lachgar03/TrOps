package com.TrOps.mvp.vehicle.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("TECHNICAL_VISIT")
@Getter
@Setter
public class TechnicalVisitDocument extends VehicleDocument {

    @Column(name = "inspection_center")
    private String inspectionCenter;

    @Override
    @Transient
    public String getDocumentReference() {
        // Since technical visits don't necessarily have a document number specified in the prompt,
        // we can return a generic string or the center name.
        return "Visite-" + inspectionCenter;
    }
}
