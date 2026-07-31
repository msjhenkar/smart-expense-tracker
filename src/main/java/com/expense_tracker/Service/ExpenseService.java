package com.expense_tracker.Service;

import com.expense_tracker.model.Expense;
import com.expense_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface ExpenseService {
    Expense addExpense(Expense expense);

    List<Expense> getAllExpense();

    List<Expense> getExpenseByCategory(String category);

    BigDecimal getTotal();

    BigDecimal getTotalByCategory(String category);

    void deleteExpense(Long id);

}
