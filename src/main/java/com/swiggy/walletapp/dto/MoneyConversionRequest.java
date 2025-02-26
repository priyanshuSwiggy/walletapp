package com.swiggy.walletapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoneyConversionRequest {
    private MoneyDto from;
    private String to_currency;
}
