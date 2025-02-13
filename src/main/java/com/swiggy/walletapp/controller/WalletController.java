package com.swiggy.walletapp.controller;

import com.swiggy.walletapp.entity.Client;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/{username}/deposit")
    public ResponseEntity<String> deposit(@PathVariable String username, @RequestParam double amount, @RequestParam Currency currency) {
        try {
            walletService.deposit(username, amount, currency);
            return new ResponseEntity<>("Deposit successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{username}/withdraw")
    public ResponseEntity<String> withdraw(@PathVariable String username, @RequestParam double amount, @RequestParam Currency currency) {
        try {
            walletService.withdraw(username, amount, currency);
            return new ResponseEntity<>("Withdrawal successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}