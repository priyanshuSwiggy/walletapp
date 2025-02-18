package com.swiggy.walletapp.dto;

import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private TransactionType transactionType;
    private double amount;
    private Currency currency;
}
