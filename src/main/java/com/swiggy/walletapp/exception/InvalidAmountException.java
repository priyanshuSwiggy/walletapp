package com.swiggy.walletapp.exception;

import org.springframework.http.HttpStatus;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message, HttpStatus status) {
        super(message);
    }
}