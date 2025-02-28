package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.MoneyDto;
import com.swiggy.walletapp.dto.TransactionDto;
import com.swiggy.walletapp.dto.TransactionResponseDto;
import com.swiggy.walletapp.entity.InterTransaction;
import com.swiggy.walletapp.entity.IntraTransaction;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import com.swiggy.walletapp.exception.InvalidTransactionTypeException;
import com.swiggy.walletapp.exception.NoTransactionsFoundException;
import com.swiggy.walletapp.exception.UnauthorizedAccessException;
import com.swiggy.walletapp.repository.InterTransactionRepository;
import com.swiggy.walletapp.repository.IntraTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final IntraTransactionRepository intraTransactionRepository;
    private final InterTransactionRepository interTransactionRepository;
    private final MoneyConversionService moneyConversionService;
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
                throw new InvalidTransactionTypeException("Invalid transaction type", HttpStatus.BAD_REQUEST);
        }
    }

    private void withdrawal(Long userId, Long walletTd, TransactionDto transactionDto) {
        Wallet wallet = walletService.withdraw(userId, walletTd, transactionDto.getCurrency(), transactionDto.getAmount());
        MoneyDto money = new MoneyDto(transactionDto.getCurrency().toString(), transactionDto.getAmount());
        MoneyDto convertedMoney = moneyConversionService.convertMoney(money, wallet.getCurrency().toString());
        double convertedAmount = convertedMoney.getAmount();
        Currency walletCurrency = wallet.getCurrency();

        IntraTransaction intraTransaction = new IntraTransaction(convertedAmount, walletCurrency, TransactionType.WITHDRAWAL, userId);
        intraTransactionRepository.save(intraTransaction);
    }

    private void deposit(Long userId, Long walletId, TransactionDto transactionDto) {
        Wallet wallet = walletService.deposit(userId, walletId, transactionDto.getCurrency(), transactionDto.getAmount());
        MoneyDto money = new MoneyDto(transactionDto.getCurrency().toString(), transactionDto.getAmount());
        MoneyDto convertedMoney = moneyConversionService.convertMoney(money, wallet.getCurrency().toString());
        double convertedAmount = convertedMoney.getAmount();
        Currency walletCurrency = wallet.getCurrency();

        IntraTransaction intraTransaction = new IntraTransaction(convertedAmount, walletCurrency, TransactionType.DEPOSIT, userId);
        intraTransactionRepository.save(intraTransaction);
    }

    private void transfer(Long senderId, Long walletId, TransactionDto transactionDto) {
        Wallet wallet = walletService.fetchUserWallet(senderId, walletId);
        Currency senderCurrency = wallet.getCurrency();
        double amount = transactionDto.getAmount();
        Wallet recipientWallet = walletService.transfer(senderId, walletId, transactionDto.getAmount(), transactionDto.getRecipientWalletId());
        MoneyDto money = new MoneyDto(senderCurrency.toString(), amount);
        MoneyDto convertedMoney = moneyConversionService.convertMoney(money, recipientWallet.getCurrency().toString());
        double convertedRecipientAmount = convertedMoney.getAmount();

        InterTransaction interTransaction = new InterTransaction(convertedRecipientAmount, recipientWallet.getCurrency(), TransactionType.TRANSFER, senderId, recipientWallet.getUser().getId());
        interTransactionRepository.save(interTransaction);
    }

    public List<TransactionResponseDto> getTransactions(Long userId, Long walletId, TransactionType transactionType) {
        checkUserAuthorization(userId, walletId);

        if(transactionType != null)
            return getTransactionsByTransactionType(userId, walletId, transactionType);

        List<IntraTransaction> intraTransactions = intraTransactionRepository.findByUserId(userId);
        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.addAll(interTransactionRepository.findByRecipientId(userId));
        interTransactions.addAll(interTransactionRepository.findBySenderId(userId));

        return mapToTransactionResponse(intraTransactions, interTransactions);
    }

    public List<TransactionResponseDto> getTransactionsByTransactionType(Long userId, Long walletId, TransactionType transactionType) {
        checkUserAuthorization(userId, walletId);

        List<InterTransaction> interTransactions = new ArrayList<>();
        List<IntraTransaction> intraTransactions = new ArrayList<>();

        if(transactionType == TransactionType.TRANSFER) {
            interTransactions.addAll(interTransactionRepository.findByRecipientId(userId));
            interTransactions.addAll(interTransactionRepository.findBySenderId(userId));
        }
        if(transactionType == TransactionType.DEPOSIT || transactionType == TransactionType.WITHDRAWAL)
            intraTransactions = intraTransactionRepository.findByUserIdAndTransactionType(userId, transactionType);

        return mapToTransactionResponse(intraTransactions, interTransactions);
    }

    private static List<TransactionResponseDto> mapToTransactionResponse(List<IntraTransaction> intraTransactions, List<InterTransaction> interTransactions) {
        if(intraTransactions.isEmpty() && interTransactions.isEmpty())
            throw new NoTransactionsFoundException("No transactions found for user", HttpStatus.NOT_FOUND);

        List<TransactionResponseDto> transactionResponse = new ArrayList<>();
        for (IntraTransaction intraTransaction : intraTransactions) {
            transactionResponse.add(new TransactionResponseDto(intraTransaction));
        }
        for (InterTransaction interTransaction : interTransactions) {
            transactionResponse.add(new TransactionResponseDto(interTransaction));
        }
        return transactionResponse;
    }

    private void checkUserAuthorization(Long userId, Long walletId) {
        if(walletService.isUnauthorizedUser(userId, walletId))
            throw new UnauthorizedAccessException("Unauthorized access to wallet", HttpStatus.UNAUTHORIZED);
    }
}