package com.swiggy.walletapp.controller;

import com.swiggy.walletapp.dto.InterTransactionDto;
import com.swiggy.walletapp.dto.IntraTransactionDto;
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

    @PostMapping("/intra-transactions")
    public ResponseEntity<String> createTransaction(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody IntraTransactionDto intraTransactionDto) {
        try {
            transactionService.createTransaction(userId, walletId, intraTransactionDto);
            return new ResponseEntity<>("Transaction successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/inter-transactions")
    public ResponseEntity<String> createTransaction(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody InterTransactionDto interTransactionDto) {
        try {
            transactionService.createTransaction(userId, walletId, interTransactionDto);
            return new ResponseEntity<>("Transaction successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}