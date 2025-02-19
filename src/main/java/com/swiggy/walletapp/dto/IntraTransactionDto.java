package com.swiggy.walletapp.dto;

import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntraTransactionDto {
    private TransactionType transactionType;
    private double amount;
    private Currency currency;
}
