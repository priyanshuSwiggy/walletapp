package com.swiggy.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiggy.walletapp.dto.TransactionRequestDto;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import com.swiggy.walletapp.exception.InsufficientFundsException;
import com.swiggy.walletapp.exception.InvalidAmountException;
import com.swiggy.walletapp.exception.UserNotFoundException;
import com.swiggy.walletapp.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerTest {

    public static final String TRANSACTION_URL = "/users/{userId}/wallets/{walletId}/transactions";

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void testProcessDeposit_Successfully_WhenDepositingValidAmount() throws Exception {
        double amount = 100.0;
        Currency currency = Currency.INR;
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doNothing().when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTION_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction successful"));
    }

    @Test
    void testProcessDeposit_Failure_WhenDepositingInvalidAmount() throws Exception {
        double amount = -100.0;
        Currency currency = Currency.INR;
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new InvalidAmountException("Deposit amount must be positive")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTION_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Deposit amount must be positive"));

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testProcessWithdrawal_Success_WhenWithdrawingValidAmount() throws Exception {
        double amount = 50.0;
        Currency currency = Currency.USD;
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.WITHDRAWAL, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doNothing().when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTION_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction successful"));
    }

    @Test
    void testProcessWithdrawal_Failure_WhenWithdrawingInvalidAmount() throws Exception {
        double amount = 500.0;
        Currency currency = Currency.USD;
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.WITHDRAWAL, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new InsufficientFundsException("Insufficient balance")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTION_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));

        assertThrows(InsufficientFundsException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testProcessTransfer_Successfully_WhenTransferringValidAmount() throws Exception {
        double amount = 100.0;
        Currency currency = Currency.INR;
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.TRANSFER, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doNothing().when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTION_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction successful"));
    }

    @Test
    void testProcessTransfer_Failure_WhenDepositingAmountGreaterThanCurrentBalance() throws Exception {
        double amount = -100.0;
        Currency currency = Currency.INR;
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.TRANSFER, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new InsufficientFundsException("Deposit amount must be greater than current balance")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTION_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Deposit amount must be greater than current balance"));

        assertThrows(InsufficientFundsException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testProcessTransfer_Failure_WhenDepositingAmountToUnregisteredRecipientWallet() throws Exception {
        double amount = -100.0;
        Currency currency = Currency.INR;
        TransactionRequestDto transactionDto = new TransactionRequestDto(TransactionType.TRANSFER, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UserNotFoundException("Amount can't be transferred to unregistered recipient wallet")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(TRANSACTION_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount can't be transferred to unregistered recipient wallet"));

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }
}