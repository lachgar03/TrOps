package com.TrOps.mvp.expense.service;

import com.TrOps.mvp.common.exception.ResourceNotFoundException;
import com.TrOps.mvp.expense.dto.ExpenseRequestDTO;
import com.TrOps.mvp.expense.dto.ExpenseResponseDTO;
import com.TrOps.mvp.expense.model.Expense;
import com.TrOps.mvp.expense.model.ExpenseCategory;
import com.TrOps.mvp.expense.repository.ExpenseRepository;
import com.TrOps.mvp.mission.model.Mission;
import com.TrOps.mvp.mission.repository.MissionRepository;
import com.TrOps.mvp.user.model.User;
import com.TrOps.mvp.vehicle.model.Vehicle;
import com.TrOps.mvp.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final VehicleRepository vehicleRepository;
    private final MissionRepository missionRepository;

    @Transactional
    public ExpenseResponseDTO createExpense(ExpenseRequestDTO request) {
        UUID companyId = getCurrentCompanyId();

        Expense expense = new Expense();
        expense.setCompanyId(companyId);
        expense.setCategory(request.category());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        expense.setDescription(request.description());
        expense.setReceiptUrl(request.receiptUrl());

        if (request.vehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(request.vehicleId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));
            expense.setVehicle(vehicle);
        }

        if (request.missionId() != null) {
            Mission mission = missionRepository.findByIdAndCompanyId(request.missionId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Mission introuvable"));
            expense.setMission(mission);
        }

        return mapToResponseDTO(expenseRepository.save(expense));
    }

    public Page<ExpenseResponseDTO> getAllExpenses(Pageable pageable) {
        UUID companyId = getCurrentCompanyId();
        return expenseRepository.findAllByCompanyId(companyId, pageable)
                .map(this::mapToResponseDTO);
    }

    public Page<ExpenseResponseDTO> getExpensesByCategory(ExpenseCategory category, Pageable pageable) {
        UUID companyId = getCurrentCompanyId();
        return expenseRepository.findAllByCompanyIdAndCategory(companyId, category, pageable)
                .map(this::mapToResponseDTO);
    }

    public ExpenseResponseDTO getExpenseById(UUID id) {
        UUID companyId = getCurrentCompanyId();
        Expense expense = expenseRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Dépense introuvable"));
        return mapToResponseDTO(expense);
    }

    @Transactional
    public ExpenseResponseDTO updateExpense(UUID id, ExpenseRequestDTO request) {
        UUID companyId = getCurrentCompanyId();
        Expense expense = expenseRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Dépense introuvable"));

        expense.setCategory(request.category());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        expense.setDescription(request.description());
        expense.setReceiptUrl(request.receiptUrl());

        if (request.vehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(request.vehicleId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable"));
            expense.setVehicle(vehicle);
        } else {
            expense.setVehicle(null);
        }

        if (request.missionId() != null) {
            Mission mission = missionRepository.findByIdAndCompanyId(request.missionId(), companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Mission introuvable"));
            expense.setMission(mission);
        } else {
            expense.setMission(null);
        }

        return mapToResponseDTO(expenseRepository.save(expense));
    }

    @Transactional
    public void deleteExpense(UUID id) {
        UUID companyId = getCurrentCompanyId();
        Expense expense = expenseRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Dépense introuvable"));
        expenseRepository.delete(expense);
    }

    private UUID getCurrentCompanyId() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getCompanyId();
    }

    private ExpenseResponseDTO mapToResponseDTO(Expense expense) {
        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getVehicle() != null ? expense.getVehicle().getId() : null,
                expense.getVehicle() != null ? expense.getVehicle().getRegistrationNumber() : null,
                expense.getMission() != null ? expense.getMission().getId() : null,
                expense.getCategory().name(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getDescription(),
                expense.getReceiptUrl(),
                expense.getCreatedAt()
        );
    }
}
