package com.expense.tracker.expenseledger.exception;

public class UnsupportedTransactionTypeException extends RuntimeException {

    public UnsupportedTransactionTypeException(String message) {
        super(message);
    }

}
