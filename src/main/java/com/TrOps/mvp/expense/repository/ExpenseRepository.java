package com.TrOps.mvp.expense.repository;

import com.TrOps.mvp.expense.model.Expense;
import com.TrOps.mvp.expense.model.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    Optional<Expense> findByIdAndCompanyId(UUID id, UUID companyId);
    Page<Expense> findAllByCompanyId(UUID companyId, Pageable pageable);
    Page<Expense> findAllByCompanyIdAndCategory(UUID companyId, ExpenseCategory category, Pageable pageable);
    Page<Expense> findAllByCompanyIdAndVehicleId(UUID companyId, UUID vehicleId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.companyId = :companyId")
    BigDecimal sumAmountByCompanyId(@Param("companyId") UUID companyId);
}
