package com.expense_tracker.service;

import com.expense_tracker.exceptions.ExpenseNotFoundException;
import com.expense_tracker.model.Expense;
import com.expense_tracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepo;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Expense expense1;
    private Expense expense2;

    @BeforeEach
    void setUp() {
        expense1 = new Expense();
        expense1.setId(1L);
        expense1.setTitle("Groceries");
        expense1.setAmount(new BigDecimal("45.50"));
        expense1.setCategory("FOOD");

        expense2 = new Expense();
        expense2.setId(2L);
        expense2.setTitle("Bus pass");
        expense2.setAmount(new BigDecimal("20.00"));
        expense2.setCategory("TRAVEL");
    }

    // ---------- addExpense ----------

    @Test
    void addExpense_savesAndReturnsExpense() {
        when(expenseRepo.save(expense1)).thenReturn(expense1);

        Expense result = expenseService.addExpense(expense1);

        assertThat(result).isEqualTo(expense1);
        verify(expenseRepo, times(1)).save(expense1);
    }

    // ---------- getAllExpense ----------

    @Test
    void getAllExpense_returnsAllExpenses() {
        when(expenseRepo.findAll()).thenReturn(List.of(expense1, expense2));

        List<Expense> result = expenseService.getAllExpense();

        assertThat(result).hasSize(2).containsExactly(expense1, expense2);
    }

    @Test
    void getAllExpense_whenEmpty_returnsEmptyList() {
        when(expenseRepo.findAll()).thenReturn(Collections.emptyList());

        List<Expense> result = expenseService.getAllExpense();

        assertThat(result).isEmpty();
    }

    // ---------- getExpenseByCategory ----------

    @Test
    void getExpenseByCategory_returnsMatchingExpenses() {
        when(expenseRepo.findByCategory("FOOD")).thenReturn(List.of(expense1));

        List<Expense> result = expenseService.getExpenseByCategory("FOOD");

        assertThat(result).containsExactly(expense1);
    }

    @Test
    void getExpenseByCategory_whenNoneMatch_returnsEmptyList() {
        when(expenseRepo.findByCategory("TRAVEL")).thenReturn(Collections.emptyList());

        List<Expense> result = expenseService.getExpenseByCategory("TRAVEL");

        assertThat(result).isEmpty();
    }

    // ---------- getTotal ----------

    @Test
    void getTotal_sumsAllExpenseAmounts() {
        when(expenseRepo.findAll()).thenReturn(List.of(expense1, expense2));

        BigDecimal result = expenseService.getTotal();

        assertThat(result).isEqualByComparingTo("65.50");
    }

    @Test
    void getTotal_whenNoExpenses_returnsZero() {
        when(expenseRepo.findAll()).thenReturn(Collections.emptyList());

        BigDecimal result = expenseService.getTotal();

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ---------- getTotalByCategory ----------

    @Test
    void getTotalByCategory_sumsMatchingExpenses() {
        when(expenseRepo.findByCategory("FOOD")).thenReturn(List.of(expense1));

        BigDecimal result = expenseService.getTotalByCategory("FOOD");

        assertThat(result).isEqualByComparingTo("45.50");
    }

    @Test
    void getTotalByCategory_whenNoMatches_returnsZero() {
        when(expenseRepo.findByCategory("TRAVEL")).thenReturn(Collections.emptyList());

        BigDecimal result = expenseService.getTotalByCategory("TRAVEL");

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ---------- deleteExpense ----------

    @Test
    void deleteExpense_whenDeleted_completesSuccessfully() {
        when(expenseRepo.deleteById(1L)).thenReturn(true);

        expenseService.deleteExpense(1L);

        verify(expenseRepo, times(1)).deleteById(1L);
    }

    @Test
    void deleteExpense_whenNotFound_throwsException() {
        when(expenseRepo.deleteById(99L)).thenReturn(false);

        ExpenseNotFoundException ex = assertThrows(ExpenseNotFoundException.class,
                () -> expenseService.deleteExpense(99L));

        assertThat(ex.getMessage()).contains("99");
    }
}