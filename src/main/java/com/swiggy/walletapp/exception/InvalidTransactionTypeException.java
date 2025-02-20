package com.swiggy.walletapp.exception;

import org.springframework.http.HttpStatus;

public class InvalidTransactionTypeException extends RuntimeException {
    public InvalidTransactionTypeException(String message, HttpStatus status) {
        super(message);
    }
}