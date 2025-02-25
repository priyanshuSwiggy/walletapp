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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CurrencyConversionService currencyConversionService;

    public boolean isUnauthorizedUser(Long userId, Long walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet not found", HttpStatus.NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));
        return !wallet.isOwnedBy(user);
    }

    public Wallet fetchUserWallet(Long userId, Long walletId) {
        if(isUnauthorizedUser(userId, walletId))
            throw new UnauthorizedAccessException("Unauthorized access to wallet", HttpStatus.NOT_FOUND);

        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Wallet deposit(Long userId, Long walletId, Currency toCurrency, double amount) {
        Wallet wallet = fetchUserWallet(userId, walletId);
        Currency fromCurrency = wallet.getCurrency();
        double convertedAmount = currencyConversionService.convertCurrency(fromCurrency.toString(), toCurrency.toString(), amount);
        wallet.deposit(convertedAmount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet withdraw(Long userId, Long walletId, Currency toCurrency, double amount) {
        Wallet wallet = fetchUserWallet(userId, walletId);
        Currency fromCurrency = wallet.getCurrency();
        double convertedAmount = currencyConversionService.convertCurrency(fromCurrency.toString(), toCurrency.toString(), amount);
        wallet.withdraw(convertedAmount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet transfer(Long userId, Long senderWalletId, double amount, Long recipientWalletId) {
        Wallet senderWallet = fetchUserWallet(userId, senderWalletId);
        Currency senderCurrency = senderWallet.getCurrency();
        withdraw(userId, senderWalletId, senderCurrency, amount);

        Wallet recipientWallet = walletRepository.findById(recipientWalletId).orElseThrow(() -> new WalletNotFoundException("Recipient wallet not found", HttpStatus.NOT_FOUND));
        User recipientUser = userRepository.findByWallet(recipientWallet).orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));
        Currency toCurrency = recipientWallet.getCurrency();
        double convertedRecipientAmount = currencyConversionService.convertCurrency(senderCurrency.toString(), toCurrency.toString(), amount);
        return deposit(recipientUser.getId(), recipientWalletId, recipientWallet.getCurrency(), convertedRecipientAmount);
    }
}
