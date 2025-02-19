package com.swiggy.walletapp.dto;

import com.swiggy.walletapp.entity.Transaction;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    private Long id;
    private TransactionType transactionType;
    private double amount;
    private Long senderId;

    public TransactionResponseDto(Optional<Transaction> byId) {
    }
}
