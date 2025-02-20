package com.swiggy.walletapp.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message, HttpStatus status) {
        super(message);
    }
}