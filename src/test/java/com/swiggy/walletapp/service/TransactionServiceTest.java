package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.InterTransactionDto;
import com.swiggy.walletapp.dto.IntraTransactionDto;
import com.swiggy.walletapp.dto.TransactionDto;
import com.swiggy.walletapp.entity.Transaction;
import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import com.swiggy.walletapp.exception.*;
import com.swiggy.walletapp.mapper.TransactionMapper;
import com.swiggy.walletapp.repository.TransactionRepository;
import com.swiggy.walletapp.repository.UserRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;
    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        walletRepository = mock(WalletRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        transactionMapper = mock(TransactionMapper.class);
        transactionService = new TransactionService(userRepository, walletRepository, transactionRepository, transactionMapper);
    }

    @Test
    public void testCreateTransaction_IntraTransaction_WalletNotFound_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, 100.0, Currency.USD);

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_IntraTransaction_UserNotFound_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, 100.0, Currency.USD);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet(new User("otherUsername", "password"), Currency.INR)));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_IntraTransaction_UnauthorizedUser_ThrowsException() {
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
    public void testCreateTransaction_DepositZeroAmount_ThrowsInvalidAmountException() {
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
    public void testCreateTransaction_DepositNegativeAmount_ThrowsInvalidAmountException() {
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
    public void testCreateTransaction_WithdrawZeroAmount_ThrowsInvalidAmountException() {
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
    public void testCreateTransaction_WithdrawNegativeAmount_ThrowsInvalidAmountException() {
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
    public void testCreateTransaction_WithdrawAmountExceedsExistingAmount_ThrowsInsufficientFundsException() {
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

    @Test
    public void testCreateTransaction_InterTransaction_ThrowsWalletNotFoundException() {
        Long userId = 1L;
        Long walletId = 1L;
        InterTransactionDto transactionDto = new InterTransactionDto(100.0, 2L);

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_InterTransaction_ThrowsUserNotFoundException() {
        Long userId = 1L;
        Long walletId = 1L;
        InterTransactionDto transactionDto = new InterTransactionDto(100.0, 2L);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet(new User("otherUsername", "password"), Currency.INR)));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_InterTransaction_ThrowsUnauthorizedUserException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        User otherUser = new User("otherUsername", "password");
        Wallet wallet = new Wallet(otherUser, Currency.INR);
        InterTransactionDto transactionDto = new InterTransactionDto(100.0, 2L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(UnauthorizedAccessException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_InterTransaction_ThrowsRecipientWalletNotFoundException() {
        Long userId = 1L;
        Long walletId = 1L;
        Long recipientId = 2L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(1000.0, user, Currency.INR);
        InterTransactionDto transactionDto = new InterTransactionDto(100.0, recipientId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.findByUserId(recipientId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransaction_TransferFromINRToINR_UpdatesBalances() {
        Long userId = 1L;
        Long walletId = 1L;
        Long recipientId = 2L;
        User user = new User("username", "password");
        User recipient = new User("recipientUsername", "password");
        Wallet senderWallet = new Wallet(1000.0, user, Currency.INR);
        Wallet recipientWallet = new Wallet(500.0, recipient, Currency.INR);
        InterTransactionDto transactionDto = new InterTransactionDto(100.0, recipientId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(recipientId)).thenReturn(Optional.of(recipientWallet));
        when(userRepository.findByWallet(recipientWallet)).thenReturn(Optional.of(recipient));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedSenderBalance = 900.0;
        double expectedRecipientBalance = 600.0;
        assertTrue(senderWallet.checkBalance(expectedSenderBalance));
        assertTrue(recipientWallet.checkBalance(expectedRecipientBalance));
        verify(walletRepository).save(senderWallet);
        verify(walletRepository).save(recipientWallet);
    }

    @Test
    public void testCreateTransaction_TransferFromUSDToINR_UpdatesBalances() {
        Long userId = 1L;
        Long walletId = 1L;
        Long recipientId = 2L;
        User user = new User("username", "password");
        User recipient = new User("recipientUsername", "password");
        Wallet senderWallet = new Wallet(1000.0, user, Currency.INR);
        Wallet recipientWallet = new Wallet(500.0, recipient, Currency.USD);
        InterTransactionDto transactionDto = new InterTransactionDto(100.0, recipientId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(recipientId)).thenReturn(Optional.of(recipientWallet));
        when(userRepository.findByWallet(recipientWallet)).thenReturn(Optional.of(recipient));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedSenderBalance = 900.0;
        double expectedRecipientBalance = 500.0 + recipientWallet.convertedAmount(Currency.INR, 100.0);
        assertTrue(senderWallet.checkBalance(expectedSenderBalance));
        assertTrue(recipientWallet.checkBalance(expectedRecipientBalance));
        verify(walletRepository).save(senderWallet);
        verify(walletRepository).save(recipientWallet);
    }

    @Test
    public void testCreateTransaction_TransferFromEURToINR_UpdatesBalances() {
        Long userId = 1L;
        Long walletId = 1L;
        Long recipientId = 2L;
        User user = new User("username", "password");
        User recipient = new User("recipientUsername", "password");
        Wallet senderWallet = new Wallet(1000.0, user, Currency.EUR);
        Wallet recipientWallet = new Wallet(500.0, recipient, Currency.INR);
        InterTransactionDto transactionDto = new InterTransactionDto(100.0, recipientId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(recipientId)).thenReturn(Optional.of(recipientWallet));
        when(userRepository.findByWallet(recipientWallet)).thenReturn(Optional.of(recipient));

        transactionService.createTransaction(userId, walletId, transactionDto);

        double expectedSenderBalance = 900.0;
        double expectedRecipientBalance = 500.0 + recipientWallet.convertedAmount(Currency.EUR, 100.0);
        assertTrue(senderWallet.checkBalance(expectedSenderBalance));
        assertTrue(recipientWallet.checkBalance(expectedRecipientBalance));
        verify(walletRepository).save(senderWallet);
        verify(walletRepository).save(recipientWallet);
    }

    @Test
    public void testGetTransactions_WalletNotFound_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    public void testGetTransactions_UserNotFound_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet(new User("otherUsername", "password"), Currency.INR)));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    public void testGetTransactions_UnauthorizedUser_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        User otherUser = new User("otherUsername", "password");
        Wallet wallet = new Wallet(otherUser, Currency.INR);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(UnauthorizedAccessException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    public void testGetTransactions_NoTransactionsFound_ThrowsException() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of());

        assertThrows(NoTransactionsFoundException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    public void testGetTransactions_SuccessfullyFetchListOfTransactions() {
        Long userId = 1L;
        Long walletId = 1L;
        User user = new User("username", "password");
        Wallet wallet = new Wallet(user, Currency.INR);
        Transaction firstTransaction = new Transaction(1L, 100.0, TransactionType.DEPOSIT, userId);
        Transaction secondTransaction = new Transaction(2L, 50.0, TransactionType.WITHDRAWAL, userId);
        List<Transaction> transactionList = List.of(firstTransaction, secondTransaction);
        List<TransactionDto> transactionDtoList = List.of(
                new TransactionDto(1L, 100.0, TransactionType.DEPOSIT, userId),
                new TransactionDto(2L, 50.0, TransactionType.WITHDRAWAL, userId)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByUserId(userId)).thenReturn(transactionList);
        when(transactionMapper.toDtoList(transactionList)).thenReturn(transactionDtoList);

        List<TransactionDto> result = transactionService.getTransactions(userId, walletId);

        assertEquals(transactionDtoList, result);
    }
}