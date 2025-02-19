package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.InterTransactionDto;
import com.swiggy.walletapp.dto.IntraTransactionDto;
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

    public void createTransaction(Long userId, Long walletId, IntraTransactionDto intraTransactionDto) {
        Wallet wallet = fetchUserWallet(userId, walletId);
        Currency currency = intraTransactionDto.getCurrency();
        double amount = intraTransactionDto.getAmount();
        double convertedAmount = wallet.convertedAmount(currency, amount);

        if(intraTransactionDto.getTransactionType() == TransactionType.DEPOSIT) {
            deposit(userId, wallet, convertedAmount);
        }
        if(intraTransactionDto.getTransactionType() == TransactionType.WITHDRAWAL)  {
            withdrawal(userId, wallet, convertedAmount);
        }
    }

    private void withdrawal(Long userId, Wallet wallet, double convertedAmount) {
        wallet.withdraw(convertedAmount);
        walletRepository.save(wallet);
        Transaction transaction = new Transaction(convertedAmount, TransactionType.WITHDRAWAL, userId);
        transactionRepository.save(transaction);
    }

    private void deposit(Long userId, Wallet wallet, double convertedAmount) {
        wallet.deposit(convertedAmount);
        walletRepository.save(wallet);
        Transaction transaction = new Transaction(convertedAmount, TransactionType.DEPOSIT, userId);
        transactionRepository.save(transaction);
    }

    public void createTransaction(Long userId, Long walletId, InterTransactionDto interTransactionDto) {
        Wallet wallet = fetchUserWallet(userId, walletId);
        Currency senderCurrency = wallet.getCurrency();
        double amount = interTransactionDto.getAmount();

        withdrawal(userId, wallet, amount);

        Long recipientId = interTransactionDto.getRecipientId();
        Wallet recipientWallet = walletRepository.findByUserId(recipientId)
                .orElseThrow(() -> new WalletNotFoundException("Recipient wallet not found"));
        double convertedRecipientAmount = recipientWallet.convertedAmount(senderCurrency, amount);
        deposit(recipientId, recipientWallet, convertedRecipientAmount);
    }

    private Wallet fetchUserWallet(Long userId, Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!wallet.isOwnedBy(user))
            throw new UnauthorizedAccessException("Unauthorized access to wallet");
        return wallet;
    }
}