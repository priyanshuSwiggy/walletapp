package com.swiggy.walletapp.exception;

import org.springframework.http.HttpStatus;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String message, HttpStatus status) {
        super(message);
    }
}
