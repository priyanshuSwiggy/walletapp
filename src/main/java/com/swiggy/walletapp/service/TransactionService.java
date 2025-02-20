package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.TransactionDto;
import com.swiggy.walletapp.dto.TransactionResponseDto;
import com.swiggy.walletapp.entity.InterTransaction;
import com.swiggy.walletapp.entity.IntraTransaction;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import com.swiggy.walletapp.exception.NoTransactionsFoundException;
import com.swiggy.walletapp.exception.UnauthorizedAccessException;
import com.swiggy.walletapp.repository.InterTransactionRepository;
import com.swiggy.walletapp.repository.IntraTransactionRepository;
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
        switch (transactionDto.getTransactionType()) {
            case DEPOSIT:
                deposit(userId, walletId, transactionDto);
                break;
            case WITHDRAWAL:
                withdrawal(userId, walletId, transactionDto);
                break;
            case TRANSFER:
                transfer(userId, walletId, transactionDto);
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type: " + transactionDto.getTransactionType());
        }
    }

    private void withdrawal(Long userId, Long walletTd, TransactionDto transactionDto) {
        Wallet wallet = walletService.withdraw(userId, walletTd, transactionDto.getCurrency(), transactionDto.getAmount());
        double convertedAmount = wallet.convertedAmount(transactionDto.getCurrency(), transactionDto.getAmount());
        Currency walletCurrency = wallet.getCurrency();

        IntraTransaction intraTransaction = new IntraTransaction(convertedAmount, walletCurrency, TransactionType.WITHDRAWAL, userId);
        intraTransactionRepository.save(intraTransaction);
    }

    private void deposit(Long userId, Long walletId, TransactionDto transactionDto) {
        Wallet wallet = walletService.deposit(userId, walletId, transactionDto.getCurrency(), transactionDto.getAmount());
        double convertedAmount = wallet.convertedAmount(transactionDto.getCurrency(), transactionDto.getAmount());
        Currency walletCurrency = wallet.getCurrency();

        IntraTransaction intraTransaction = new IntraTransaction(convertedAmount, walletCurrency, TransactionType.DEPOSIT, userId);
        intraTransactionRepository.save(intraTransaction);
    }

    private void transfer(Long senderId, Long walletId, TransactionDto transactionDto) {
        Wallet wallet = walletService.fetchUserWallet(senderId, walletId);
        Currency senderCurrency = wallet.getCurrency();
        double amount = transactionDto.getAmount();
        Wallet recipientWallet = walletService.transfer(senderId, walletId, transactionDto.getAmount(), transactionDto.getRecipientWalletId());
        double convertedRecipientAmount = recipientWallet.convertedAmount(senderCurrency, amount);

        InterTransaction interTransaction = new InterTransaction(convertedRecipientAmount, recipientWallet.getCurrency(), TransactionType.TRANSFER, senderId, recipientWallet.getUser().getId());
        interTransactionRepository.save(interTransaction);
    }

    public List<TransactionResponseDto> getTransactions(Long userId, Long walletId) {
        if(walletService.isUserIsUnauthorized(userId, walletId))
            throw new UnauthorizedAccessException("Unauthorized access to wallet");
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