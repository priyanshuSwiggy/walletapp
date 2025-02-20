package com.swiggy.walletapp.controller;

import com.swiggy.walletapp.dto.TransactionDto;
import com.swiggy.walletapp.dto.TransactionResponseDto;
import com.swiggy.walletapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/wallets/{walletId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(@PathVariable Long userId, @PathVariable Long walletId) {
        List<TransactionResponseDto> transactions = transactionService.getTransactions(userId, walletId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createTransaction(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody TransactionDto transactionDto) {
        transactionService.createTransaction(userId, walletId, transactionDto);
        return new ResponseEntity<>("Transaction successful", HttpStatus.CREATED);
    }
}