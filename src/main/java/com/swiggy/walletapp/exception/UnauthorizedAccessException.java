package com.swiggy.walletapp.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message, HttpStatus status) {
        super(message);
    }
}