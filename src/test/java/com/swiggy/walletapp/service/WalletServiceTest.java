//package com.swiggy.walletapp.service;
//
//import com.swiggy.walletapp.entity.User;
//import com.swiggy.walletapp.entity.Wallet;
//import com.swiggy.walletapp.enums.Currency;
//import com.swiggy.walletapp.exception.UnauthorizedAccessException;
//import com.swiggy.walletapp.exception.UserNotFoundException;
//import com.swiggy.walletapp.exception.WalletNotFoundException;
//import com.swiggy.walletapp.repository.UserRepository;
//import com.swiggy.walletapp.repository.WalletRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class WalletServiceTest {
//
//    private UserRepository userRepository;
//    private WalletRepository walletRepository;
//    private WalletService walletService;
//    private CurrencyConversionService currencyConversionService;
//
//    @BeforeEach
//    void setUp() {
//        userRepository = mock(UserRepository.class);
//        walletRepository = mock(WalletRepository.class);
//        currencyConversionService = mock(CurrencyConversionService.class);
//        walletService = new WalletService(userRepository, walletRepository, currencyConversionService);
//    }
//
//    @Test
//    public void testIsUnauthorizedUserThrowsWalletNotFoundExceptionWhenWalletNotFound() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());
//
//        assertThrows(WalletNotFoundException.class, () -> walletService.isUnauthorizedUser(userId, walletId));
//    }
//
//    @Test
//    public void testIsUnauthorizedUserThrowsUserNotFoundExceptionWhenUserNotFound() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        final Wallet wallet = new Wallet();
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        assertThrows(UserNotFoundException.class, () -> walletService.isUnauthorizedUser(userId, walletId));
//    }
//
//    @Test
//    public void testIsUnauthorizedUserReturnsTrueWhenUnauthorizedUser() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        final User user = new User("username", "password");
//        final User otherUser = new User("otherUsername", "password");
//        final Wallet wallet = new Wallet(otherUser, Currency.INR);
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        assertTrue(walletService.isUnauthorizedUser(userId, walletId));
//    }
//
//    @Test
//    public void testIsUnauthorizedUserReturnsFalseWhenAuthorizedUser() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        final User user = new User("username", "password");
//        final Wallet wallet = new Wallet(user, Currency.INR);
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        assertFalse(walletService.isUnauthorizedUser(userId, walletId));
//    }
//
//    @Test
//    public void testFetchUserWalletThrowsWalletNotFoundExceptionWhenWalletNotFound() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());
//
//        assertThrows(WalletNotFoundException.class, () -> walletService.fetchUserWallet(userId, walletId));
//    }
//
//    @Test
//    public void testFetchUserWalletThrowsUserNotFoundExceptionWhenUserNotFound() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet()));
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        assertThrows(UserNotFoundException.class, () -> walletService.fetchUserWallet(userId, walletId));
//    }
//
//    @Test
//    public void testFetchUserWalletThrowsUnauthorizedAccessExceptionWhenUnauthorizedUser() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        final User user = new User("username", "password");
//        final User otherUser = new User("otherUsername", "password");
//        final Wallet wallet = new Wallet(otherUser, Currency.INR);
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        assertThrows(UnauthorizedAccessException.class, () -> walletService.fetchUserWallet(userId, walletId));
//    }
//
//    @Test
//    public void testFetchUserWalletReturnsWalletWhenAuthorizedUser() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        final User user = new User("username", "password");
//        final Wallet wallet = new Wallet(user, Currency.INR);
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        Wallet result = walletService.fetchUserWallet(userId, walletId);
//
//        assertEquals(wallet, result);
//    }
//
//    @Test
//    public void testDepositIncreasesBalanceFrom1000To1100WhenDepositing100INRToINR() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        final User user = new User("username", "password");
//        final Wallet wallet = new Wallet(1000.0, user, Currency.INR);
//        final double amount = 100.0;
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(currencyConversionService.convertMoney("INR", "INR", amount)).thenReturn(100.0);
//
//        walletService.deposit(userId, walletId, Currency.INR, amount);
//
//        assertTrue(wallet.checkBalance(1100.0));
//        verify(walletRepository).save(wallet);
//    }
//
//    @Test
//    public void testDepositIncreasesBalanceFrom1000To9300WhenDepositing100USDToINR() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        final User user = new User("username", "password");
//        final Wallet wallet = new Wallet(1000.0, user, Currency.INR);
//        final double amount = 100.0;
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(currencyConversionService.convertMoney("INR", "USD", amount)).thenReturn(8300.0);
//
//        walletService.deposit(userId, walletId, Currency.USD, amount);
//
//        assertTrue(wallet.checkBalance(9300.0));
//        verify(walletRepository).save(wallet);
//    }
//
//    @Test
//    public void testWithdrawDecreasesBalanceFrom1000To900WhenWithdrawing100INRToINR() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        final User user = new User("username", "password");
//        final Wallet wallet = new Wallet(1000.0, user, Currency.INR);
//        final double amount = 100.0;
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(currencyConversionService.convertMoney("INR", "INR", amount)).thenReturn(100.0);
//
//        walletService.withdraw(userId, walletId, Currency.INR, amount);
//
//        assertTrue(wallet.checkBalance(900.0));
//        verify(walletRepository).save(wallet);
//    }
//
//    @Test
//    public void testWithdrawDecreasesBalanceFrom1000To917WhenWithdrawing1USDToINR() {
//        final Long userId = 1L;
//        final Long walletId = 1L;
//        final User user = new User("username", "password");
//        final Wallet wallet = new Wallet(1000.0, user, Currency.INR);
//        final double amount = 1.0;
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(currencyConversionService.convertMoney("INR", "USD", amount)).thenReturn(83.0);
//
//        walletService.withdraw(userId, walletId, Currency.USD, amount);
//
//        assertTrue(wallet.checkBalance(917.0));
//        verify(walletRepository).save(wallet);
//    }
//
//    @Test
//    public void testTransferThrowsWalletNotFoundExceptionWhenRecipientWalletNotFound() {
//        final Long userId = 1L;
//        final Long senderWalletId = 1L;
//        final Long recipientWalletId = 2L;
//        final User sender = new User(userId, "senderUsername", "password");
//        final Wallet senderWallet = new Wallet(1000.0, sender, Currency.INR);
//        final double amount = 100.0;
//        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
//        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.empty());
//        when(currencyConversionService.convertMoney("INR", "INR", amount)).thenReturn(100.0);
//
//        assertThrows(WalletNotFoundException.class, () -> walletService.transfer(userId, senderWalletId, amount, recipientWalletId));
//    }
//
//    @Test
//    public void testTransferThrowsUserNotFoundExceptionWhenRecipientUserNotFound() {
//        final Long userId = 1L;
//        final Long senderWalletId = 1L;
//        final Long recipientWalletId = 2L;
//        final User sender = new User(userId, "senderUsername", "password");
//        final Wallet senderWallet = new Wallet(1000.0, sender, Currency.INR);
//        final Wallet recipientWallet = new Wallet(500.0, new User(2L, "recipientUsername", "password"), Currency.INR);
//        final double amount = 100.0;
//        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
//        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(recipientWallet));
//        when(userRepository.findByWallet(recipientWallet)).thenReturn(Optional.empty());
//        when(currencyConversionService.convertMoney("INR", "INR", amount)).thenReturn(100.0);
//
//        assertThrows(UserNotFoundException.class, () -> walletService.transfer(userId, senderWalletId, amount, recipientWalletId));
//    }
//
//    @Test
//    public void testTransferDecreasesSenderBalanceFrom1000To900AndIncreasesRecipientBalanceFrom500To600WhenTransferring100INRToINR() {
//        final Long userId = 1L;
//        final Long senderWalletId = 1L;
//        final Long recipientUserId = 2L;
//        final Long recipientWalletId = 2L;
//        final User sender = new User(userId, "senderUsername", "password");
//        final User recipient = new User(recipientUserId, "recipientUsername", "password");
//        final Wallet senderWallet = new Wallet(1000.0, sender, Currency.INR);
//        final Wallet recipientWallet = new Wallet(500.0, recipient, Currency.INR);
//        final double amount = 100.0;
//        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
//        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(recipientWallet));
//        when(userRepository.findByWallet(recipientWallet)).thenReturn(Optional.of(recipient));
//        when(userRepository.findById(recipientUserId)).thenReturn(Optional.of(recipient));
//        when(currencyConversionService.convertMoney("INR", "INR", amount)).thenReturn(100.0);
//
//        walletService.transfer(userId, senderWalletId, amount, recipientWalletId);
//
//        assertTrue(senderWallet.checkBalance(900.0));
//        assertTrue(recipientWallet.checkBalance(600.0));
//        verify(walletRepository).save(senderWallet);
//        verify(walletRepository).save(recipientWallet);
//    }
//
//    @Test
//    public void testTransferDecreasesSenderBalanceFrom1000To900AndIncreasesRecipientBalanceFrom500To1333WhenTransferring100USDToINR() {
//        final Long userId = 1L;
//        final Long senderWalletId = 1L;
//        final Long recipientUserId = 2L;
//        final Long recipientWalletId = 2L;
//        final User sender = new User(userId, "senderUsername", "password");
//        final User recipient = new User(recipientUserId, "recipientUsername", "password");
//        final Wallet senderWallet = new Wallet(1000.0, sender, Currency.USD);
//        final Wallet recipientWallet = new Wallet(500.0, recipient, Currency.INR);
//        final double amount = 100.0;
//        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
//        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(recipientWallet));
//        when(userRepository.findByWallet(recipientWallet)).thenReturn(Optional.of(recipient));
//        when(userRepository.findById(recipientUserId)).thenReturn(Optional.of(recipient));
//        when(currencyConversionService.convertMoney("USD", "USD", amount)).thenReturn(100.0);
//        when(currencyConversionService.convertMoney("USD", "INR", amount)).thenReturn(8300.0);
//        when(currencyConversionService.convertMoney("INR", "INR", 8300.0)).thenReturn(8300.0);
//
//
//        walletService.transfer(userId, senderWalletId, amount, recipientWalletId);
//
//        assertTrue(senderWallet.checkBalance(900.0));
//        assertTrue(recipientWallet.checkBalance(8800.0));
//        verify(walletRepository).save(senderWallet);
//        verify(walletRepository).save(recipientWallet);
//    }
//}