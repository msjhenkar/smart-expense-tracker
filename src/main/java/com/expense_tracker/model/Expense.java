package com.expense_tracker.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    private Long id;

    @NotBlank(message = "Title is required")
    private String name;

    @NotBlank(message = "Amount is required")
    @Positive( message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message="Category is required")
    private String category;

    private LocalDate localDate;
}
