package com.swiggy.walletapp.service;

import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.UnauthorizedAccessException;
import com.swiggy.walletapp.exception.UserNotFoundException;
import com.swiggy.walletapp.exception.WalletNotFoundException;
import com.swiggy.walletapp.repository.UserRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    boolean isAuthorizedUser(Long userId, Long walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return wallet.isOwnedBy(user);
    }

    public Wallet fetchUserWallet(Long userId, Long walletId) {
        if(!isAuthorizedUser(userId, walletId))
            throw new UnauthorizedAccessException("Unauthorized access to wallet");

        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
    }

    public void deposit(Long userId, Wallet wallet, Currency currency, double amount) {
        double convertedAmount = wallet.convertedAmount(currency, amount);
        wallet.deposit(convertedAmount);
        walletRepository.save(wallet);
    }

    public void withdraw(Long userId, Wallet wallet, Currency currency, double amount) {
        double convertedAmount = wallet.convertedAmount(currency, amount);
        wallet.withdraw(convertedAmount);
        walletRepository.save(wallet);
    }
}
