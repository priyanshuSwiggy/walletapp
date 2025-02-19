package com.swiggy.walletapp.controller;

import com.swiggy.walletapp.dto.TransactionRequestDto;
import com.swiggy.walletapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/wallets/{walletId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<String> createTransaction(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody TransactionRequestDto transactionRequestDto) {
        try {
            transactionService.createTransaction(userId, walletId, transactionRequestDto);
            return new ResponseEntity<>("Transaction successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/transfers/{recipientId}")
    public ResponseEntity<String> processTransfer(@PathVariable Long userId, @PathVariable Long walletId, @PathVariable Long recipientId, @RequestBody TransactionRequestDto transactionRequestDto) {
        try {
            transactionService.processTransfer(userId, walletId, recipientId, transactionRequestDto);
            return new ResponseEntity<>("Transaction successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}