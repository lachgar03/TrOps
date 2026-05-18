package com.TrOps.mvp.expense.dto;

import com.TrOps.mvp.expense.model.ExpenseCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseRequestDTO(
        UUID vehicleId,
        UUID missionId,

        @NotNull(message = "La catégorie est obligatoire")
        ExpenseCategory category,

        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit être positif")
        BigDecimal amount,

        @NotNull(message = "La date de dépense est obligatoire")
        LocalDate expenseDate,

        @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
        String description,

        String receiptUrl
) {}
