package com.swiggy.walletapp.dto;

import com.swiggy.walletapp.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterTransactionDto {
    private double amount;
    private Long recipientId;
}
