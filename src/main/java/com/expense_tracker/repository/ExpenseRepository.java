package com.expense_tracker.repository;


import com.expense_tracker.model.Expense;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository {
    Expense save(Expense expense);

    List<Expense> findAll();

    List<Expense> findByCategory(String category);

    Optional<Expense> findById(Long id);

    boolean deleteById(Long id);
}
