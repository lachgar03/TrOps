package com.TrOps.mvp.vehicle.dto;

import java.time.LocalDate;
import java.util.UUID;

public record DocumentResponseDTO(
        UUID id,
        String documentType,
        String documentNumber,
        LocalDate issueDate,
        LocalDate expirationDate
) {
}
