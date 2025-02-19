package com.swiggy.walletapp.dto;

import com.swiggy.walletapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private double amount;
    private TransactionType transactionType;
    private Long userId;
}
