package com.swiggy.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swiggy.walletapp.dto.TransactionDto;
import com.swiggy.walletapp.dto.TransactionResponseDto;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import com.swiggy.walletapp.exception.*;
import com.swiggy.walletapp.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerTest {

    public static final String TRANSACTIONS_URL = "/users/{senderId}/wallets/{walletId}/transactions";

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateTransactionThrowsWalletNotFoundExceptionWhenWalletNotFound() throws Exception {
        double amount = 100.0;
        Currency currency = Currency.INR;
        TransactionDto transactionDto = new TransactionDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new WalletNotFoundException("Wallet not found", HttpStatus.NOT_FOUND)).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found"));

        assertThrows(WalletNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransactionThrowsUserNotFoundExceptionWhenUserNotFound() throws Exception {
        double amount = 100.0;
        Currency currency = Currency.INR;
        TransactionDto transactionDto = new TransactionDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UserNotFoundException("User not found", HttpStatus.NOT_FOUND)).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransactionThrowsUnauthorizedAccessExceptionWhenUnauthorizedUser() throws Exception {
        double amount = 100.0;
        Currency currency = Currency.INR;
        TransactionDto transactionDto = new TransactionDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UnauthorizedAccessException("Unauthorized access to wallet", HttpStatus.UNAUTHORIZED)).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized access to wallet"));

        assertThrows(UnauthorizedAccessException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransactionReturnsCreatedWhenDepositingValidAmount() throws Exception {
        double amount = 100.0;
        Currency currency = Currency.INR;
        TransactionDto transactionDto = new TransactionDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doNothing().when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transaction successful"));
    }

    @Test
    void testCreateTransactionThrowsInvalidAmountExceptionWhenDepositingInvalidAmount() throws Exception {
        double amount = -100.0;
        Currency currency = Currency.INR;
        TransactionDto transactionDto = new TransactionDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new InvalidAmountException("Deposit amount must be positive", HttpStatus.BAD_REQUEST)).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Deposit amount must be positive"));

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransactionThrowsInvalidTransactionTypeExceptionWhenTransactionTypeIsInvalid() throws Exception {
        double amount = -100.0;
        Currency currency = Currency.INR;
        TransactionDto transactionDto = new TransactionDto(null, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new InvalidTransactionTypeException("Invalid transaction type", HttpStatus.BAD_REQUEST)).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid transaction type"));

        assertThrows(InvalidTransactionTypeException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransactionReturnsCreatedWhenWithdrawingValidAmount() throws Exception {
        double amount = 50.0;
        Currency currency = Currency.USD;
        TransactionDto transactionDto = new TransactionDto(TransactionType.WITHDRAWAL, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doNothing().when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transaction successful"));
    }

    @Test
    void testCreateTransactionThrowsInsufficientFundsExceptionWhenWithdrawingInvalidAmount() throws Exception {
        double amount = 500.0;
        Currency currency = Currency.USD;
        TransactionDto transactionDto = new TransactionDto(TransactionType.WITHDRAWAL, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new InsufficientFundsException("Insufficient balance", HttpStatus.BAD_REQUEST)).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));

        assertThrows(InsufficientFundsException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransactionReturnsCreatedWhenTransferringValidAmount() throws Exception {
        double amount = 100.0;
        Long recipientId = 2L;
        TransactionDto transactionDto = new TransactionDto(TransactionType.TRANSFER, amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doNothing().when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transaction successful"));
    }

    @Test
    void testCreateTransactionThrowsInsufficientFundsExceptionWhenTransferAmountGreaterThanCurrentBalance() throws Exception {
        double amount = 100.0;
        Long recipientId = 2L;
        TransactionDto transactionDto = new TransactionDto(TransactionType.TRANSFER, amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new InsufficientFundsException("Transfer amount should be less than current balance", HttpStatus.BAD_REQUEST)).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Transfer amount should be less than current balance"));

        assertThrows(InsufficientFundsException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransactionThrowsUserNotFoundExceptionWhenTransferringAmountToUnregisteredRecipientWallet() throws Exception {
        double amount = 100.0;
        Long recipientId = 2L;
        TransactionDto transactionDto = new TransactionDto(TransactionType.TRANSFER, amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UserNotFoundException("Amount can't be transferred to unregistered recipient wallet", HttpStatus.NOT_FOUND)).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Amount can't be transferred to unregistered recipient wallet"));

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransactionThrowsWalletNotFoundExceptionWhenInterTransactionWalletNotFound() throws Exception {
        double amount = 100.0;
        Long recipientId = 1L;
        TransactionDto transactionDto = new TransactionDto(TransactionType.TRANSFER, amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new WalletNotFoundException("Wallet not found", HttpStatus.NOT_FOUND)).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found"));

        assertThrows(WalletNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransactionThrowsUserNotFoundExceptionWhenInterTransactionUserNotFound() throws Exception {
        double amount = 100.0;
        Long recipientId = 1L;
        TransactionDto transactionDto = new TransactionDto(TransactionType.TRANSFER, amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UserNotFoundException("User not found", HttpStatus.NOT_FOUND)).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransactionThrowsUnauthorizedAccessExceptionWhenInterTransactionUnauthorizedUser() throws Exception {
        double amount = 100.0;
        Long recipientId = 1L;
        TransactionDto transactionDto = new TransactionDto(TransactionType.TRANSFER, amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UnauthorizedAccessException("Unauthorized access to wallet", HttpStatus.UNAUTHORIZED)).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized access to wallet"));

        assertThrows(UnauthorizedAccessException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testGetTransactionsThrowsWalletNotFoundExceptionWhenWalletNotFound() throws Exception {
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new WalletNotFoundException("Wallet not found", HttpStatus.NOT_FOUND)).when(transactionService).getTransactions(userId, walletId);

        mockMvc.perform(get(TRANSACTIONS_URL, userId, walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThrows(WalletNotFoundException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    void testGetTransactionsThrowsUserNotFoundExceptionWhenUserNotFound() throws Exception {
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UserNotFoundException("User not found", HttpStatus.NOT_FOUND)).when(transactionService).getTransactions(userId, walletId);

        mockMvc.perform(get(TRANSACTIONS_URL, userId, walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThrows(UserNotFoundException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    void testGetTransactionsThrowsUnauthorizedAccessExceptionWhenUnauthorizedUser() throws Exception {
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UnauthorizedAccessException("Unauthorized access to wallet", HttpStatus.UNAUTHORIZED)).when(transactionService).getTransactions(userId, walletId);

        mockMvc.perform(get(TRANSACTIONS_URL, userId, walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThrows(UnauthorizedAccessException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    void testGetTransactionsThrowsNoTransactionsFoundExceptionWhenNoTransactionsFound() throws Exception {
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new NoTransactionsFoundException("No transactions found for user", HttpStatus.NOT_FOUND)).when(transactionService).getTransactions(userId, walletId);

        mockMvc.perform(get(TRANSACTIONS_URL, userId, walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThrows(NoTransactionsFoundException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    void testGetTransactionsReturnsOkWhenSuccessfullyFetchListOfTransactions() throws Exception {
        Long recipientId = 2L;
        Long senderId = 1L;
        Long walletId = 1L;

        TransactionResponseDto firstTransactionResponseDto = new TransactionResponseDto(1L, 100.0, Currency.INR, TransactionType.DEPOSIT, recipientId, null, LocalDateTime.now());
        TransactionResponseDto secondTransactionResponseDto = new TransactionResponseDto(2L, 100.0, Currency.INR, TransactionType.DEPOSIT, recipientId, senderId, LocalDateTime.now());
        when(transactionService.getTransactions(recipientId, walletId)).thenReturn(List.of(firstTransactionResponseDto, secondTransactionResponseDto));

        MvcResult mvcResult = mockMvc.perform(get(TRANSACTIONS_URL, recipientId, walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String expectedResponse = objectMapper.writeValueAsString(List.of(firstTransactionResponseDto, secondTransactionResponseDto));
        String actualResponse = mvcResult.getResponse().getContentAsString();

        JSONAssert.assertEquals(expectedResponse, actualResponse, false);
    }
}