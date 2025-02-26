package com.swiggy.walletapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoneyDto {
    private String currency;
    private double amount;
}
