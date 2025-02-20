package com.swiggy.walletapp.service;

import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.UnauthorizedAccessException;
import com.swiggy.walletapp.exception.UserNotFoundException;
import com.swiggy.walletapp.exception.WalletNotFoundException;
import com.swiggy.walletapp.repository.UserRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WalletServiceTest {

    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        walletRepository = mock(WalletRepository.class);
        walletService = new WalletService(userRepository, walletRepository);
    }

    @Test
    public void testIsUnauthorizedUserThrowsWalletNotFoundExceptionWhenWalletNotFound() {
        Long userId = 1L;
        Long walletId = 1L;

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.isUnauthorizedUser(userId, walletId));
    }

    @Test
    public void testIsUnauthorizedUserThrowsUserNotFoundExceptionWhenUserNotFound() {
        Long userId = 1L;
        Long walletId = 1L;
        Wallet wallet = new Wallet();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> walletService.isUnauthorizedUser(userId, walletId));
    }

    @Test
    public void testIsUnauthorizedUserReturnsTrueWhenUnauthorizedUser() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        User otherUser = new User("otherUsername", "password");
        Wallet wallet = new Wallet(otherUser, Currency.INR);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertTrue(walletService.isUnauthorizedUser(userId, walletId));
    }

    @Test
    public void testIsUnauthorizedUserReturnsFalseWhenAuthorizedUser() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertFalse(walletService.isUnauthorizedUser(userId, walletId));
    }

    @Test
    public void testFetchUserWalletThrowsWalletNotFoundExceptionWhenWalletNotFound() {
        Long userId = 1L;
        Long walletId = 1L;

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.fetchUserWallet(userId, walletId));
    }

    @Test
    public void testFetchUserWalletThrowsUserNotFoundExceptionWhenUserNotFound() {
        Long userId = 1L;
        Long walletId = 1L;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> walletService.fetchUserWallet(userId, walletId));
    }

    @Test
    public void testFetchUserWalletThrowsUnauthorizedAccessExceptionWhenUnauthorizedUser() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        User otherUser = new User("otherUsername", "password");
        Wallet wallet = new Wallet(otherUser, Currency.INR);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedAccessException.class, () -> walletService.fetchUserWallet(userId, walletId));
    }

    @Test
    public void testFetchUserWalletReturnsWalletWhenAuthorizedUser() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Wallet result = walletService.fetchUserWallet(userId, walletId);

        assertEquals(wallet, result);
    }

    @Test
    public void testDepositIncreasesBalanceFrom1000To1100WhenDepositing100INRToINR() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(1000.0, user, Currency.INR);
        double amount = 100.0;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        walletService.deposit(userId, walletId, Currency.INR, amount);

        assertTrue(wallet.checkBalance(1100.0));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void testDepositIncreasesBalanceFrom1000To9300WhenDepositing100USDToINR() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(1000.0, user, Currency.INR);
        double amount = 100.0;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        walletService.deposit(userId, walletId, Currency.USD, amount);

        assertTrue(wallet.checkBalance(9300.0));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void testWithdrawDecreasesBalanceFrom1000To900WhenWithdrawing100INRToINR() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(1000.0, user, Currency.INR);
        double amount = 100.0;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        walletService.withdraw(userId, walletId, Currency.INR, amount);

        assertTrue(wallet.checkBalance(900.0));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void testWithdrawDecreasesBalanceFrom1000To917WhenWithdrawing1USDToINR() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(1000.0, user, Currency.INR);
        double amount = 1.0;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        walletService.withdraw(userId, walletId, Currency.USD, amount);

        assertTrue(wallet.checkBalance(917.0));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void testTransferThrowsWalletNotFoundExceptionWhenRecipientWalletNotFound() {
        Long userId = 1L;
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        User sender = new User(userId, "senderUsername", "password");
        Wallet senderWallet = new Wallet(1000.0, sender, Currency.INR);
        double amount = 100.0;

        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.transfer(userId, senderWalletId, amount, recipientWalletId));
    }

    @Test
    public void testTransferThrowsUserNotFoundExceptionWhenRecipientUserNotFound() {
        Long userId = 1L;
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        User sender = new User(userId, "senderUsername", "password");
        Wallet senderWallet = new Wallet(1000.0, sender, Currency.INR);
        Wallet recipientWallet = new Wallet(500.0, new User(2L, "recipientUsername", "password"), Currency.INR);
        double amount = 100.0;

        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(recipientWallet));
        when(userRepository.findByWallet(recipientWallet)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> walletService.transfer(userId, senderWalletId, amount, recipientWalletId));
    }

    @Test
    public void testTransferDecreasesSenderBalanceFrom1000To900AndIncreasesRecipientBalanceFrom500To600WhenTransferring100INRToINR() {
        Long userId = 1L;
        Long senderWalletId = 1L;
        Long recipientUserId = 2L;
        Long recipientWalletId = 2L;
        User sender = new User(userId, "senderUsername", "password");
        User recipient = new User(recipientUserId, "recipientUsername", "password");
        Wallet senderWallet = new Wallet(1000.0, sender, Currency.INR);
        Wallet recipientWallet = new Wallet(500.0, recipient, Currency.INR);
        double amount = 100.0;

        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(recipientWallet));
        when(userRepository.findByWallet(recipientWallet)).thenReturn(Optional.of(recipient));
        when(userRepository.findById(recipientUserId)).thenReturn(Optional.of(recipient));

        walletService.transfer(userId, senderWalletId, amount, recipientWalletId);

        assertTrue(senderWallet.checkBalance(900.0));
        assertTrue(recipientWallet.checkBalance(600.0));
        verify(walletRepository).save(senderWallet);
        verify(walletRepository).save(recipientWallet);
    }

    @Test
    public void testTransferDecreasesSenderBalanceFrom1000To900AndIncreasesRecipientBalanceFrom500To1333WhenTransferring100USDToINR() {
        Long userId = 1L;
        Long senderWalletId = 1L;
        Long recipientUserId = 2L;
        Long recipientWalletId = 2L;
        User sender = new User(userId, "senderUsername", "password");
        User recipient = new User(recipientUserId, "recipientUsername", "password");
        Wallet senderWallet = new Wallet(1000.0, sender, Currency.USD);
        Wallet recipientWallet = new Wallet(500.0, recipient, Currency.INR);
        double amount = 100.0;

        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(recipientWallet));
        when(userRepository.findByWallet(recipientWallet)).thenReturn(Optional.of(recipient));
        when(userRepository.findById(recipientUserId)).thenReturn(Optional.of(recipient));

        walletService.transfer(userId, senderWalletId, amount, recipientWalletId);

        assertTrue(senderWallet.checkBalance(900.0));
        assertTrue(recipientWallet.checkBalance(8800.0));
        verify(walletRepository).save(senderWallet);
        verify(walletRepository).save(recipientWallet);
    }
}