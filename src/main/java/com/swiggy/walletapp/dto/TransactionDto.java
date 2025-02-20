package com.swiggy.walletapp.dto;

import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private TransactionType transactionType;
    private double amount;
    private Currency currency;
    private Long recipientWalletId;

    public TransactionDto(TransactionType transactionType, double amount, Long recipientWalletId) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.recipientWalletId = recipientWalletId;
    }

    public TransactionDto(TransactionType transactionType, double amount, Currency currency) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.currency = currency;
    }
}
