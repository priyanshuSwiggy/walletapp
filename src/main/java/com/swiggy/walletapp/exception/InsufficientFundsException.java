package com.swiggy.walletapp.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message, HttpStatus status) {
        super(message);
    }
}