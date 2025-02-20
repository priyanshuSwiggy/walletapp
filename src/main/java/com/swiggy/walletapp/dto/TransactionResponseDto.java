package com.swiggy.walletapp.dto;

import com.swiggy.walletapp.entity.InterTransaction;
import com.swiggy.walletapp.entity.IntraTransaction;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    private Long id;
    private double amount;
    private Currency currency;
    private TransactionType transactionType;
    private Long recipientId;
    private Long senderId;
    private LocalDateTime timestamp;

    public TransactionResponseDto(IntraTransaction intraTransaction) {
        this.id = intraTransaction.getId();
        this.amount = intraTransaction.getAmount();
        this.currency = intraTransaction.getCurrency();
        this.transactionType = intraTransaction.getTransactionType();
        this.recipientId = intraTransaction.getUserId();
        this.senderId = null;
        this.timestamp = intraTransaction.getTimestamp();
    }

    public TransactionResponseDto(InterTransaction interTransaction) {
        this.id = interTransaction.getId();
        this.amount = interTransaction.getAmount();
        this.currency = interTransaction.getCurrency();
        this.transactionType = interTransaction.getTransactionType();
        this.recipientId = interTransaction.getRecipientId();
        this.senderId = interTransaction.getSenderId();
        this.timestamp = interTransaction.getTimestamp();
    }
}
