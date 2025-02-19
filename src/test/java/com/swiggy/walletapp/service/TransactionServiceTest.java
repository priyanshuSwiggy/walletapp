package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.TransactionRequestDto;
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
    public void testProcessTransaction_WalletNotFound_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.DEPOSIT, 100.0, Currency.USD);

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testProcessTransaction_UserNotFound_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.DEPOSIT, 100.0, Currency.USD);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet(new User("otherUsername", "password"), Currency.INR)));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testProcessTransaction_UnauthorizedUser_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        User otherUser = new User("otherUsername", "password");
        Wallet wallet = new Wallet(otherUser, Currency.INR);
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.DEPOSIT, 100.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(UnauthorizedAccessException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testProcessDeposit_ZeroAmount_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.DEPOSIT, 0.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testProcessDeposit_NegativeAmount_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.DEPOSIT, -100.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testProcessDeposit_ValidUser_UpdatesBalance() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.DEPOSIT, 100.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedBalance = 100.0 * Currency.USD.getConversionRate();
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void testProcessDeposit_ValidUserEUR_UpdatesBalance() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.DEPOSIT, 100.0, Currency.EUR);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedBalance = 100.0 * Currency.EUR.getConversionRate();
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void testProcessWithdraw_ZeroAmount_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.WITHDRAWAL, 0.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testProcessWithdraw_NegativeAmount_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.WITHDRAWAL, -100.0, Currency.USD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testProcessWithdrawal_InsufficientFunds_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.WITHDRAWAL, 100.0, Currency.USD);

        wallet.deposit(50.0 * Currency.USD.getConversionRate());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InsufficientFundsException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testProcessWithdrawal_ValidUser_UpdatesBalance() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.WITHDRAWAL, 100.0, Currency.USD);

        wallet.deposit(200.0 * Currency.USD.getConversionRate());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedBalance = 100.0 * Currency.USD.getConversionRate();
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void testProcessWithdrawal_ValidUserEUR_UpdatesBalance() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.WITHDRAWAL, 100.0, Currency.EUR);

        wallet.deposit(200.0 * Currency.EUR.getConversionRate());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedBalance = 100.0 * Currency.EUR.getConversionRate();
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
    }

}