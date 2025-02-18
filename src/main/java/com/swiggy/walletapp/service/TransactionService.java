package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.TransactionDto;
import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import com.swiggy.walletapp.exception.*;
import com.swiggy.walletapp.repository.UserRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public void processDepositOrWithdrawal(Long userId, Long walletId, TransactionDto transactionDto) {
        Wallet wallet = fetchUserWallet(userId, walletId, transactionDto);
        Currency currency = transactionDto.getCurrency();
        double amountInINR = currency.convertToINR(transactionDto.getAmount());

        if (transactionDto.getTransactionType() == TransactionType.DEPOSIT) {
            wallet.deposit(amountInINR);
        }
        if (transactionDto.getTransactionType() == TransactionType.WITHDRAW) {
            wallet.withdraw(amountInINR);
        }

        walletRepository.save(wallet);
    }

    private Wallet fetchUserWallet(Long userId, Long walletId, TransactionDto transactionDto) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!wallet.isOwnedBy(user)) {
            throw new UnauthorizedAccessException("Unauthorized access to wallet");
        }
        return wallet;
    }
}