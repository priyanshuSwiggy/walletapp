package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.TransactionRequestDto;
import com.swiggy.walletapp.dto.TransactionResponseDto;
import com.swiggy.walletapp.entity.Transaction;
import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import com.swiggy.walletapp.exception.UnauthorizedAccessException;
import com.swiggy.walletapp.exception.UserNotFoundException;
import com.swiggy.walletapp.exception.WalletNotFoundException;
import com.swiggy.walletapp.repository.TransactionRepository;
import com.swiggy.walletapp.repository.UserRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public void createTransaction(Long userId, Long walletId, TransactionRequestDto transactionRequestDto) {
        Wallet wallet = fetchUserWallet(userId, walletId);
        Currency senderCurrency = transactionRequestDto.getCurrency();
        double amount = transactionRequestDto.getAmount();
        double convertedAmount = wallet.convertAmount(senderCurrency, amount);

        if(transactionRequestDto.getTransactionType() == TransactionType.DEPOSIT) {
            wallet.deposit(convertedAmount);
            walletRepository.save(wallet);
            Transaction transaction = new Transaction(convertedAmount, TransactionType.DEPOSIT, userId);
            transactionRepository.save(transaction);
        }
        if(transactionRequestDto.getTransactionType() == TransactionType.WITHDRAWAL)  {
            wallet.withdraw(convertedAmount);
            walletRepository.save(wallet);
            Transaction transaction = new Transaction(convertedAmount, TransactionType.WITHDRAWAL, userId);
            transactionRepository.save(transaction);
        }
    }

    private Wallet fetchUserWallet(Long userId, Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!wallet.isOwnedBy(user)) {
            throw new UnauthorizedAccessException("Unauthorized access to wallet");
        }
        return wallet;
    }

    public void processTransfer(Long userId, Long walletId, Long recipientId, TransactionRequestDto transactionRequestDto) {
    }
}