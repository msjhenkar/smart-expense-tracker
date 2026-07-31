package com.expense_tracker.Controller;

import com.expense_tracker.Service.ExpenseService;
import com.expense_tracker.dto.ExpenseRequest;
import com.expense_tracker.model.Expense;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseController {
    private final ExpenseService service;

    public ExpenseController (ExpenseService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Expense> addExpense(@RequestBody ExpenseRequest request){
        Expense expense = new Expense(
                null,
                request.getTitle(),
                request.getAmount(),
                request.getCategory(),
                request.getDate()
        );
        Expense saved = service.addExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(@RequestParam(required = false) String category){
        List<Expense> expenses = (category == null)
                ? service.getAllExpense()
                : service.getExpenseByCategory(category);

        return ResponseEntity.ok(expenses);

    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotal(@RequestParam(required = false) String category){
        BigDecimal total;

        if(category == null){
            total = service.getTotal();
        }else {
            total = service.getTotalByCategory(category);
        }

        Map<String, BigDecimal> response = new HashMap<>();
        response.put("total", total);

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        service.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
