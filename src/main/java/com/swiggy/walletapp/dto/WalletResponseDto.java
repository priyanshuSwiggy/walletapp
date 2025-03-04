package com.swiggy.walletapp.dto;

import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseDto {
    private Long walletId;
    private double balance;
    private Currency currency;

    public WalletResponseDto(Wallet wallet) {
        this.walletId = wallet.getId();
        this.balance = wallet.getBalance();
        this.currency = wallet.getCurrency();
    }
}
