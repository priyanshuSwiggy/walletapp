package com.swiggy.walletapp.dto;

import com.swiggy.walletapp.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private String username;
    private String password;
    private Currency currency;
}
