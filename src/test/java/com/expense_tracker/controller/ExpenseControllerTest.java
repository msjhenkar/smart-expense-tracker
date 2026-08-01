package com.expense_tracker.controller;

import com.expense_tracker.service.ExpenseService;
import com.expense_tracker.dto.ExpenseRequest;
import com.expense_tracker.exceptions.ExpenseNotFoundException;
import com.expense_tracker.model.Expense;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService expenseService;

    private final JsonMapper objectMapper = JsonMapper.builder().build();

    @Test
    void addExpense_validRequest_returns201WithSavedExpense() throws Exception {
        ExpenseRequest request = new ExpenseRequest();
        request.setTitle("Groceries");
        request.setAmount(new BigDecimal("450.00"));
        request.setCategory("Food");
        request.setDate(LocalDate.of(2026, 7, 30));

        Expense saved = new Expense(1L, "Groceries", new BigDecimal("450.00"), "Food", LocalDate.of(2026, 7, 30));

        when(expenseService.addExpense(any(Expense.class))).thenReturn(saved);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Groceries"))
                .andExpect(jsonPath("$.category").value("Food"));
    }

    @Test
    void addExpense_negativeAmount_returns400() throws Exception {
        ExpenseRequest request = new ExpenseRequest();
        request.setTitle("Groceries");
        request.setAmount(new BigDecimal("-50.00"));
        request.setCategory("Food");
        request.setDate(LocalDate.of(2026, 7, 30));

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.amount").exists());

        verify(expenseService, never()).addExpense(any());
    }

    @Test
    void addExpense_missingTitle_returns400() throws Exception {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setCategory("Food");
        request.setDate(LocalDate.of(2026, 7, 30));

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void getExpenses_noFilter_returnsAll() throws Exception {
        List<Expense> expenses = List.of(
                new Expense(1L, "Groceries", new BigDecimal("450.00"), "Food", LocalDate.of(2026, 7, 30)),
                new Expense(2L, "Uber", new BigDecimal("200.00"), "Travel", LocalDate.of(2026, 7, 29))
        );
        when(expenseService.getAllExpense()).thenReturn(expenses);

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(expenseService).getAllExpense();
        verify(expenseService, never()).getExpenseByCategory(any());
    }

    @Test
    void getExpenses_withCategoryFilter_returnsFiltered() throws Exception {
        List<Expense> foodExpenses = List.of(
                new Expense(1L, "Groceries", new BigDecimal("450.00"), "Food", LocalDate.of(2026, 7, 30))
        );
        when(expenseService.getExpenseByCategory("Food")).thenReturn(foodExpenses);

        mockMvc.perform(get("/api/expenses").param("category", "Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("Food"));

        verify(expenseService).getExpenseByCategory("Food");
    }

    @Test
    void getTotal_noFilter_returnsOverallTotal() throws Exception {
        when(expenseService.getTotal()).thenReturn(new BigDecimal("650.00"));

        mockMvc.perform(get("/api/expenses/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(650.00));
    }

    @Test
    void getTotal_withCategory_returnsCategoryTotal() throws Exception {
        when(expenseService.getTotalByCategory("Food")).thenReturn(new BigDecimal("450.00"));

        mockMvc.perform(get("/api/expenses/total").param("category", "Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(450.00));
    }

    @Test
    void deleteExpense_existingId_returns204() throws Exception {
        doNothing().when(expenseService).deleteExpense(1L);

        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isNoContent());

        verify(expenseService).deleteExpense(1L);
    }

    @Test
    void deleteExpense_nonExistentId_returns404() throws Exception {
        doThrow(new ExpenseNotFoundException("Expense not found with id: 99"))
                .when(expenseService).deleteExpense(99L);

        mockMvc.perform(delete("/api/expenses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Expense not found with id: 99"));
    }
}
