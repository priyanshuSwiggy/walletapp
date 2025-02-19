package com.swiggy.walletapp.exception;

public class NoTransactionsFoundException extends RuntimeException {
    public NoTransactionsFoundException(String message) {
        super(message);
    }
}
