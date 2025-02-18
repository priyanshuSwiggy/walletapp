package com.swiggy.walletapp.controller;

import com.swiggy.walletapp.dto.TransactionDto;
import com.swiggy.walletapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/wallets")
public class TransactionController {

    private final TransactionService transactionService;

    @PutMapping("/{walletId}/transactions")
    public ResponseEntity<String> processDepositOrWithdrawal(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody TransactionDto transactionDto) {
        try {
            transactionService.processDepositOrWithdrawal(userId, walletId, transactionDto);
            return new ResponseEntity<>("Transaction successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}