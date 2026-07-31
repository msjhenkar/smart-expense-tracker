package com.expense_tracker.exceptions;

public class ExpenseNotFoundException extends RuntimeException{
    public ExpenseNotFoundException(String message){
        super(message);
    }
}
