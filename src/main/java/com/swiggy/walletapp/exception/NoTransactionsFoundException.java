package com.swiggy.walletapp.exception;

import org.springframework.http.HttpStatus;

public class NoTransactionsFoundException extends RuntimeException {
    public NoTransactionsFoundException(String message, HttpStatus status) {
        super(message);
    }
}
