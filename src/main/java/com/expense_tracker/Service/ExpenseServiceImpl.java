package com.expense_tracker.Service;

import com.expense_tracker.exceptions.ExpenseNotFoundException;
import com.expense_tracker.model.Expense;
import com.expense_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

public class ExpenseServiceImpl implements ExpenseService{

    @Autowired
    private ExpenseRepository expenseRepo;

    public Expense addExpense(Expense expense){
        return expenseRepo.save(expense);
    }

    public List<Expense> getAllExpense(){
        return expenseRepo.findAll();
    }

    public List<Expense> getExpenseByCategory(String category){
        return expenseRepo.findByCategory(category);
    }

    public BigDecimal getTotal(){
        List<Expense> expenses = expenseRepo.findAll();

        BigDecimal total = BigDecimal.ZERO;

        for(Expense expense : expenses){
            total = total.add(expense.getAmount());
        }

        return total;
    }

    public BigDecimal getTotalByCategory(String category){
        List<Expense> expenses = expenseRepo.findByCategory(category);

        BigDecimal total = BigDecimal.ZERO;

        for(Expense expense : expenses){
            total = total.add(expense.getAmount());
        }
        return total;
    }

    public void deleteExpense(Long id){
        boolean deleted = expenseRepo.deleteById(id);

        if(!deleted) {
            throw new ExpenseNotFoundException("Expense Not found with id :" + id);
        }
    }

}
