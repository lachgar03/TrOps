package com.TrOps.mvp.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExpenseResponseDTO(
        UUID id,
        UUID vehicleId,
        String vehicleRegistrationNumber,
        UUID missionId,
        String category,
        BigDecimal amount,
        LocalDate expenseDate,
        String description,
        String receiptUrl,
        LocalDateTime createdAt
) {}
