package com.TrOps.mvp.vehicle.dto;

import com.TrOps.mvp.vehicle.model.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DocumentRequestDTO(
        @NotNull(message = "Document type is required") DocumentType documentType,
        @NotBlank(message = "Document number is required") String documentNumber,
        @NotNull(message = "Issue date is required") LocalDate issueDate,
        @NotNull(message = "Expiration date is required") LocalDate expirationDate
) {
}
