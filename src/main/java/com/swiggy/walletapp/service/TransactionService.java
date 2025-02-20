package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.TransactionDto;
import com.swiggy.walletapp.dto.TransactionResponseDto;
import com.swiggy.walletapp.entity.InterTransaction;
import com.swiggy.walletapp.entity.IntraTransaction;
import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import com.swiggy.walletapp.exception.NoTransactionsFoundException;
import com.swiggy.walletapp.exception.UnauthorizedAccessException;
import com.swiggy.walletapp.exception.UserNotFoundException;
import com.swiggy.walletapp.exception.WalletNotFoundException;
import com.swiggy.walletapp.repository.InterTransactionRepository;
import com.swiggy.walletapp.repository.IntraTransactionRepository;
import com.swiggy.walletapp.repository.UserRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final IntraTransactionRepository intraTransactionRepository;
    private final InterTransactionRepository interTransactionRepository;
    private final WalletService walletService;

    public void createTransaction(Long userId, Long walletId, TransactionDto transactionDto) {
        Wallet wallet = walletService.fetchUserWallet(userId, walletId);

        switch (transactionDto.getTransactionType()) {
            case DEPOSIT:
                deposit(userId, wallet, transactionDto);
                break;
            case WITHDRAWAL:
                withdrawal(userId, wallet, transactionDto);
                break;
            case TRANSFER:
                transfer(userId, wallet, transactionDto);
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type: " + transactionDto.getTransactionType());
        }
    }

    private void withdrawal(Long userId, Wallet wallet, TransactionDto transactionDto) {
        walletService.withdraw(userId, wallet, transactionDto.getCurrency(), transactionDto.getAmount());
        double convertedAmount = wallet.convertedAmount(transactionDto.getCurrency(), transactionDto.getAmount());
        Currency walletCurrency = wallet.getCurrency();
        IntraTransaction intraTransaction = new IntraTransaction(convertedAmount, walletCurrency, TransactionType.WITHDRAWAL, userId);
        intraTransactionRepository.save(intraTransaction);
    }

    private void deposit(Long userId, Wallet wallet, TransactionDto transactionDto) {
        walletService.deposit(userId, wallet, transactionDto.getCurrency(), transactionDto.getAmount());
        double convertedAmount = wallet.convertedAmount(transactionDto.getCurrency(), transactionDto.getAmount());
        Currency walletCurrency = wallet.getCurrency();
        IntraTransaction intraTransaction = new IntraTransaction(convertedAmount, walletCurrency, TransactionType.DEPOSIT, userId);
        intraTransactionRepository.save(intraTransaction);
    }

    private void transfer(Long senderId, Wallet wallet, TransactionDto transactionDto) {
        Currency senderCurrency = wallet.getCurrency();
        double amount = transactionDto.getAmount();
        walletService.withdraw(senderId, wallet, senderCurrency, amount);

        Long recipientWalletId = transactionDto.getRecipientWalletId();
        Wallet recipientWallet = walletService.fetchUserWallet(recipientWalletId, recipientWalletId);
        double convertedRecipientAmount = recipientWallet.convertedAmount(senderCurrency, amount);
        walletService.deposit(senderId, recipientWallet, recipientWallet.getCurrency(), convertedRecipientAmount);

        InterTransaction interTransaction = new InterTransaction(convertedRecipientAmount, recipientWallet.getCurrency(), TransactionType.TRANSFER, senderId, recipientWallet.getUser().getId());
        interTransactionRepository.save(interTransaction);
    }

    public List<TransactionResponseDto> getTransactions(Long userId, Long walletId) {
        walletService.fetchUserWallet(userId, walletId);
        List<IntraTransaction> intraTransactions = intraTransactionRepository.findByUserId(userId);
        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.addAll(interTransactionRepository.findByRecipientId(userId));
        interTransactions.addAll(interTransactionRepository.findBySenderId(userId));
        if(intraTransactions.isEmpty() && interTransactions.isEmpty())
            throw new NoTransactionsFoundException("No transactions found for user");
        List<TransactionResponseDto> transactionResponse = new ArrayList<>();
        for (IntraTransaction intraTransaction : intraTransactions) {
            transactionResponse.add(new TransactionResponseDto(intraTransaction));
        }
        for (InterTransaction interTransaction : interTransactions) {
            transactionResponse.add(new TransactionResponseDto(interTransaction));
        }
        return transactionResponse;
    }
}