package com.swiggy.walletapp.dto;

import com.swiggy.walletapp.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletRequestDto {
    private Currency currency;
}
