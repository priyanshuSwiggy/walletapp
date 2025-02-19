package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.IntraTransactionDto;
import com.swiggy.walletapp.entity.Transaction;
import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import com.swiggy.walletapp.exception.*;
import com.swiggy.walletapp.repository.TransactionRepository;
import com.swiggy.walletapp.repository.UserRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;


    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        walletRepository = mock(WalletRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(userRepository, walletRepository, transactionRepository);
    }

    @Test
    public void testCreateTransaction_WalletNotFound_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, 100.0, Currency.USD);

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_UserNotFound_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, 100.0, Currency.USD);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet(new User("otherUsername", "password"), Currency.INR)));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_UnauthorizedUser_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        User otherUser = new User("otherUsername", "password");
        Wallet wallet = new Wallet(otherUser, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, 100.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(UnauthorizedAccessException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_DepositZeroAmount_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, 0.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_DepositNegativeAmount_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, -100.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_DepositAmountInINR_UpdatesBalanceInINR() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(1000.0, user, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, 100.0, Currency.INR);
        Transaction transaction = new Transaction(wallet.convertedAmount(Currency.INR, 100.0), TransactionType.DEPOSIT, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedBalance = 1100.0;
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transaction);
    }

    @Test
    public void testCreateTransaction_DepositAmountInUSD_UpdatesBalanceInINR() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(1000.0, user, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, 100.0, Currency.USD);
        Transaction transaction = new Transaction(wallet.convertedAmount(Currency.USD, 100.0), TransactionType.DEPOSIT, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedBalance = 9300.0;
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transaction);
    }

    @Test
    public void testCreateTransaction_DepositAmountInEUR_UpdatesBalanceInINR() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(1000.0, user, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, 100.0, Currency.EUR);
        Transaction transaction = new Transaction(wallet.convertedAmount(Currency.EUR, 100.0), TransactionType.DEPOSIT, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedBalance = 10000.0;
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transaction);
    }

    @Test
    public void testCreateTransaction_WithdrawZeroAmount_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.WITHDRAWAL, 0.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_WithdrawNegativeAmount_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.WITHDRAWAL, -100.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_WithdrawAmountExceedsExistingAmount_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.WITHDRAWAL, 100.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InsufficientFundsException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_WithdrawAmountInINR_UpdatesBalanceInINR() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(1000.0, user, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.WITHDRAWAL, 10.0, Currency.INR);
        Transaction transaction = new Transaction(wallet.convertedAmount(Currency.INR, 10.0), TransactionType.WITHDRAWAL, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedBalance = 990.0;
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transaction);
    }

    @Test
    public void testCreateTransaction_WithdrawAmountInUSD_UpdatesBalanceInINR() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(1000.0, user, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.WITHDRAWAL, 10.0, Currency.USD);
        Transaction transaction = new Transaction(wallet.convertedAmount(Currency.USD, 10.0), TransactionType.WITHDRAWAL, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedBalance = 170.0;
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transaction);
    }

    @Test
    public void testCreateTransaction_WithdrawAmountInEUR_UpdatesBalanceInINR() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(1000.0, user, Currency.INR);
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.WITHDRAWAL, 10.0, Currency.EUR);
        Transaction transaction = new Transaction(wallet.convertedAmount(Currency.EUR, 10.0), TransactionType.WITHDRAWAL, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedBalance = 100.0;
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transaction);
    }

}