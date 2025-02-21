package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.TransactionDto;
import com.swiggy.walletapp.dto.TransactionResponseDto;
import com.swiggy.walletapp.entity.InterTransaction;
import com.swiggy.walletapp.entity.IntraTransaction;
import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import com.swiggy.walletapp.exception.InvalidTransactionTypeException;
import com.swiggy.walletapp.exception.NoTransactionsFoundException;
import com.swiggy.walletapp.exception.UnauthorizedAccessException;
import com.swiggy.walletapp.repository.InterTransactionRepository;
import com.swiggy.walletapp.repository.IntraTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private IntraTransactionRepository intraTransactionRepository;
    private InterTransactionRepository interTransactionRepository;
    private WalletService walletService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        intraTransactionRepository = mock(IntraTransactionRepository.class);
        interTransactionRepository = mock(InterTransactionRepository.class);
        walletService = mock(WalletService.class);
        transactionService = new TransactionService(intraTransactionRepository, interTransactionRepository, walletService);
    }

    @Test
    public void testCreateTransactionThrowsInvalidTransactionTypeExceptionWhenTransactionTypeIsInvalid() {
        final Long userId = 1L;
        final Long walletId = 1L;
        final TransactionDto transactionDto = new TransactionDto(TransactionType.DEFAULT, 100.0, Currency.INR);

        assertThrows(InvalidTransactionTypeException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    public void testCreateTransactionDepositIncreasesBalanceFrom1000To1100WhenDepositing100() {
        final Long userId = 1L;
        final Long walletId = 1L;
        final TransactionDto transactionDto = new TransactionDto(TransactionType.DEPOSIT, 100.0, Currency.INR);
        final Wallet wallet = new Wallet(1000.0, new User(userId, "username", "password"), Currency.INR);
        when(walletService.deposit(userId, walletId, Currency.INR, 100.0)).thenReturn(wallet);

        transactionService.createTransaction(userId, walletId, transactionDto);

        verify(walletService).deposit(userId, walletId, Currency.INR, 100.0);
        verify(intraTransactionRepository).save(any(IntraTransaction.class));
    }

    @Test
    public void testCreateTransactionDepositIncreasesBalanceFrom2000To2200WhenDepositing200() {
        final Long userId = 2L;
        final Long walletId = 2L;
        final TransactionDto transactionDto = new TransactionDto(TransactionType.DEPOSIT, 200.0, Currency.USD);
        final Wallet wallet = new Wallet(2000.0, new User(userId, "anotherUsername", "password"), Currency.USD);
        when(walletService.deposit(userId, walletId, Currency.USD, 200.0)).thenReturn(wallet);

        transactionService.createTransaction(userId, walletId, transactionDto);

        verify(walletService).deposit(userId, walletId, Currency.USD, 200.0);
        verify(intraTransactionRepository).save(any(IntraTransaction.class));
    }

    @Test
    public void testCreateTransactionWithdrawalDecreasesBalanceFrom1000To900WhenWithdrawing100() {
        final Long userId = 1L;
        final Long walletId = 1L;
        final TransactionDto transactionDto = new TransactionDto(TransactionType.WITHDRAWAL, 100.0, Currency.INR);
        final Wallet wallet = new Wallet(1000.0, new User(userId, "username", "password"), Currency.INR);
        when(walletService.withdraw(userId, walletId, Currency.INR, 100.0)).thenReturn(wallet);

        transactionService.createTransaction(userId, walletId, transactionDto);

        verify(walletService).withdraw(userId, walletId, Currency.INR, 100.0);
        verify(intraTransactionRepository).save(any(IntraTransaction.class));
    }

    @Test
    public void testCreateTransactionWithdrawalDecreasesBalanceFrom2000To1800WhenWithdrawing200() {
        final Long userId = 2L;
        final Long walletId = 2L;
        final TransactionDto transactionDto = new TransactionDto(TransactionType.WITHDRAWAL, 200.0, Currency.USD);
        final Wallet wallet = new Wallet(2000.0, new User(userId, "anotherUsername", "password"), Currency.USD);
        when(walletService.withdraw(userId, walletId, Currency.USD, 200.0)).thenReturn(wallet);

        transactionService.createTransaction(userId, walletId, transactionDto);

        verify(walletService).withdraw(userId, walletId, Currency.USD, 200.0);
        verify(intraTransactionRepository).save(any(IntraTransaction.class));
    }

    @Test
    public void testCreateTransactionTransferDecreasesSenderBalanceFrom1000To900AndIncreasesRecipientBalanceFrom500To600WhenTransferring100() {
        final Long userId = 1L;
        final Long senderWalletId = 1L;
        final Long recipientWalletId = 2L;
        final TransactionDto transactionDto = new TransactionDto(TransactionType.TRANSFER, 100.0, Currency.INR, recipientWalletId);
        final Wallet senderWallet = new Wallet(1000.0, new User(userId, "senderUsername", "password"), Currency.INR);
        final Wallet recipientWallet = new Wallet(500.0, new User(2L, "recipientUsername", "password"), Currency.INR);
        when(walletService.fetchUserWallet(userId, senderWalletId)).thenReturn(senderWallet);
        when(walletService.transfer(userId, senderWalletId, 100.0, recipientWalletId)).thenReturn(recipientWallet);

        transactionService.createTransaction(userId, senderWalletId, transactionDto);

        verify(walletService).transfer(userId, senderWalletId, 100.0, recipientWalletId);
        verify(interTransactionRepository).save(any(InterTransaction.class));
    }

    @Test
    public void testCreateTransactionTransferDecreasesSenderBalanceFrom2000To1800AndIncreasesRecipientBalanceFrom1000To1200WhenTransferring200() {
        final Long userId = 2L;
        final Long senderWalletId = 2L;
        final Long recipientWalletId = 3L;
        final TransactionDto transactionDto = new TransactionDto(TransactionType.TRANSFER, 200.0, Currency.USD, recipientWalletId);
        final Wallet senderWallet = new Wallet(2000.0, new User(userId, "anotherSenderUsername", "password"), Currency.USD);
        final Wallet recipientWallet = new Wallet(1000.0, new User(3L, "anotherRecipientUsername", "password"), Currency.USD);
        when(walletService.fetchUserWallet(userId, senderWalletId)).thenReturn(senderWallet);
        when(walletService.transfer(userId, senderWalletId, 200.0, recipientWalletId)).thenReturn(recipientWallet);

        transactionService.createTransaction(userId, senderWalletId, transactionDto);

        verify(walletService).transfer(userId, senderWalletId, 200.0, recipientWalletId);
        verify(interTransactionRepository).save(any(InterTransaction.class));
    }

    @Test
    public void testGetTransactionsThrowsUnauthorizedAccessExceptionWhenUnauthorizedUser() {
        final Long userId = 1L;
        final Long walletId = 1L;
        when(walletService.isUnauthorizedUser(userId, walletId)).thenReturn(true);

        assertThrows(UnauthorizedAccessException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    public void testGetTransactionsThrowsNoTransactionsFoundExceptionWhenNoTransactionsFound() {
        final Long userId = 1L;
        final Long walletId = 1L;
        when(walletService.isUnauthorizedUser(userId, walletId)).thenReturn(false);
        when(intraTransactionRepository.findByUserId(userId)).thenReturn(List.of());
        when(interTransactionRepository.findByRecipientId(userId)).thenReturn(List.of());
        when(interTransactionRepository.findBySenderId(userId)).thenReturn(List.of());

        assertThrows(NoTransactionsFoundException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    public void testGetTransactionsReturnsListOfTransactionsWhenTransactionsExist() {
        final Long userId = 1L;
        final Long walletId = 1L;
        Wallet wallet = new Wallet(new User(userId, "username", "password"), Currency.INR);
        IntraTransaction intraTransaction = new IntraTransaction(100.0, Currency.INR, TransactionType.DEPOSIT, userId);
        InterTransaction interTransaction = new InterTransaction(100.0, Currency.INR, TransactionType.TRANSFER, userId, 2L);
        when(walletService.isUnauthorizedUser(userId, walletId)).thenReturn(false);
        when(intraTransactionRepository.findByUserId(userId)).thenReturn(List.of(intraTransaction));
        when(interTransactionRepository.findByRecipientId(userId)).thenReturn(List.of(interTransaction));
        when(interTransactionRepository.findBySenderId(userId)).thenReturn(List.of());

        List<TransactionResponseDto> transactions = transactionService.getTransactions(userId, walletId);

        assertEquals(2, transactions.size());
    }

    @Test
    public void testGetTransactionsReturnsListOfTransactionsWhenDifferentInputs() {
        final Long userId = 2L;
        final Long walletId = 2L;
        final Wallet wallet = new Wallet(new User(userId, "anotherUsername", "password"), Currency.USD);
        final IntraTransaction intraTransaction = new IntraTransaction(200.0, Currency.USD, TransactionType.WITHDRAWAL, userId);
        final InterTransaction interTransaction = new InterTransaction(200.0, Currency.USD, TransactionType.TRANSFER, userId, 3L);
        when(walletService.isUnauthorizedUser(userId, walletId)).thenReturn(false);
        when(intraTransactionRepository.findByUserId(userId)).thenReturn(List.of(intraTransaction));
        when(interTransactionRepository.findByRecipientId(userId)).thenReturn(List.of(interTransaction));
        when(interTransactionRepository.findBySenderId(userId)).thenReturn(List.of());

        List<TransactionResponseDto> transactions = transactionService.getTransactions(userId, walletId);

        assertEquals(2, transactions.size());
    }

    @Test
    public void testGetTransactionsByTransactionTypeReturnsListOfDepositTransactionsWhenTransactionTypeIsDeposit() {
        final Long userId = 1L;
        final Long walletId = 1L;
        final Wallet wallet = new Wallet(new User(userId, "username", "password"), Currency.INR);
        final IntraTransaction firstIntraTransaction = new IntraTransaction(100.0, Currency.INR, TransactionType.DEPOSIT, userId);
        final IntraTransaction secondIntraTransaction = new IntraTransaction(200.0, Currency.INR, TransactionType.DEPOSIT, userId);
        final IntraTransaction thirdIntraTransaction = new IntraTransaction(300.0, Currency.INR, TransactionType.DEPOSIT, userId);
        final IntraTransaction fourthIntraTransaction = new IntraTransaction(400.0, Currency.INR, TransactionType.WITHDRAWAL, userId);
        final InterTransaction interTransaction = new InterTransaction(100.0, Currency.INR, TransactionType.TRANSFER, userId, 2L);
        when(walletService.isUnauthorizedUser(userId, walletId)).thenReturn(false);
        when(intraTransactionRepository.findByUserId(userId)).thenReturn(List.of(firstIntraTransaction, secondIntraTransaction, thirdIntraTransaction));

        List<TransactionResponseDto> transactions = transactionService.getTransactions(userId, walletId);

        assertEquals(3, transactions.size());
        assertEquals(TransactionType.DEPOSIT, transactions.get(0).getTransactionType());
        assertEquals(TransactionType.DEPOSIT, transactions.get(1).getTransactionType());
        assertEquals(TransactionType.DEPOSIT, transactions.get(2).getTransactionType());
    }

    @Test
    public void testGetTransactionsByTransactionTypeReturnsListOfTransactionsWhenTransactionTypeIsWithdrawal() {
        final Long userId = 1L;
        final Long walletId = 1L;
        final Wallet wallet = new Wallet(new User(userId, "username", "password"), Currency.INR);
        final IntraTransaction firstIntraTransaction = new IntraTransaction(100.0, Currency.INR, TransactionType.DEPOSIT, userId);
        final IntraTransaction secondIntraTransaction = new IntraTransaction(200.0, Currency.INR, TransactionType.DEPOSIT, userId);
        final IntraTransaction thirdIntraTransaction = new IntraTransaction(300.0, Currency.INR, TransactionType.DEPOSIT, userId);
        final IntraTransaction fourthIntraTransaction = new IntraTransaction(400.0, Currency.INR, TransactionType.WITHDRAWAL, userId);
        final InterTransaction interTransaction = new InterTransaction(100.0, Currency.INR, TransactionType.TRANSFER, userId, 2L);
        when(walletService.isUnauthorizedUser(userId, walletId)).thenReturn(false);
        when(intraTransactionRepository.findByUserId(userId)).thenReturn(List.of(fourthIntraTransaction));

        List<TransactionResponseDto> transactions = transactionService.getTransactions(userId, walletId);

        assertEquals(1, transactions.size());
        assertEquals(TransactionType.WITHDRAWAL, transactions.get(0).getTransactionType());
    }

    @Test
    public void testGetTransactionsByTransactionTypeReturnsListOfTransactionsWhenTransactionTypeIsTransfer() {
        final Long userId = 1L;
        final Long walletId = 1L;
        final Wallet wallet = new Wallet(new User(userId, "username", "password"), Currency.INR);
        final IntraTransaction firstIntraTransaction = new IntraTransaction(100.0, Currency.INR, TransactionType.DEPOSIT, userId);
        final IntraTransaction secondIntraTransaction = new IntraTransaction(200.0, Currency.INR, TransactionType.DEPOSIT, userId);
        final IntraTransaction thirdIntraTransaction = new IntraTransaction(300.0, Currency.INR, TransactionType.DEPOSIT, userId);
        final IntraTransaction fourthIntraTransaction = new IntraTransaction(400.0, Currency.INR, TransactionType.WITHDRAWAL, userId);
        final InterTransaction firstInterTransaction = new InterTransaction(100.0, Currency.INR, TransactionType.TRANSFER, 1L, 2L);
        final InterTransaction secondInterTransaction = new InterTransaction(100.0, Currency.INR, TransactionType.TRANSFER, 1L, 2L);
        final InterTransaction thirdInterTransaction = new InterTransaction(100.0, Currency.INR, TransactionType.TRANSFER, 2L, 1L);
        final InterTransaction fourthInterTransaction = new InterTransaction(100.0, Currency.INR, TransactionType.TRANSFER, 2L, 1L);
        when(walletService.isUnauthorizedUser(userId, walletId)).thenReturn(false);
        when(interTransactionRepository.findByRecipientId(userId)).thenReturn(List.of(thirdInterTransaction, fourthInterTransaction));
        when(interTransactionRepository.findBySenderId(userId)).thenReturn(List.of(firstInterTransaction, secondInterTransaction));

        List<TransactionResponseDto> transactions = transactionService.getTransactions(userId, walletId);

        assertEquals(4, transactions.size());
        assertEquals(TransactionType.TRANSFER, transactions.get(0).getTransactionType());
        assertEquals(TransactionType.TRANSFER, transactions.get(1).getTransactionType());
        assertEquals(TransactionType.TRANSFER, transactions.get(2).getTransactionType());
        assertEquals(TransactionType.TRANSFER, transactions.get(3).getTransactionType());
    }
}