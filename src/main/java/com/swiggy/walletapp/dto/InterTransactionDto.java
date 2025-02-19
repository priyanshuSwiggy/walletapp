package com.swiggy.walletapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterTransactionDto {
    private double amount;
    private Long recipientWalletId;
}
