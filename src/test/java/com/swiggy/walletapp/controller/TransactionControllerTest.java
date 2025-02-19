package com.swiggy.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiggy.walletapp.dto.InterTransactionDto;
import com.swiggy.walletapp.dto.IntraTransactionDto;
import com.swiggy.walletapp.dto.TransactionDto;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerTest {

    public static final String FETCH_TRANSACTIONS_URL = "/users/{userId}/wallets/{walletId}/transactions";
    public static final String INTRA_TRANSACTIONS_URL = "/users/{userId}/wallets/{walletId}/transactions/intra-transactions";
    public static final String INTER_TRANSACTIONS_URL = "/users/{userId}/wallets/{walletId}/transactions/inter-transactions";

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
    void testCreateTransaction_IntraTransaction_WalletNotFound_ThrowsException() throws Exception {
        double amount = 100.0;
        Currency currency = Currency.INR;
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new WalletNotFoundException("Wallet not found")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTRA_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Wallet not found"));

        assertThrows(WalletNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransaction_IntraTransaction_UserNotFound_ThrowsException() throws Exception {
        double amount = 100.0;
        Currency currency = Currency.INR;
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UserNotFoundException("User not found")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTRA_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransaction_IntraTransaction_UnauthorizedUser_ThrowsException() throws Exception {
        double amount = 100.0;
        Currency currency = Currency.INR;
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UnauthorizedAccessException("Unauthorized access to wallet")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTRA_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unauthorized access to wallet"));

        assertThrows(UnauthorizedAccessException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransaction_Success_WhenDepositingValidAmount() throws Exception {
        double amount = 100.0;
        Currency currency = Currency.INR;
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doNothing().when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTRA_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transaction successful"));
    }

    @Test
    void testCreateTransaction_Failure_WhenDepositingInvalidAmount() throws Exception {
        double amount = -100.0;
        Currency currency = Currency.INR;
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.DEPOSIT, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new InvalidAmountException("Deposit amount must be positive")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTRA_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Deposit amount must be positive"));

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransaction_Success_WhenWithdrawingValidAmount() throws Exception {
        double amount = 50.0;
        Currency currency = Currency.USD;
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.WITHDRAWAL, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doNothing().when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTRA_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transaction successful"));
    }

    @Test
    void testCreateTransaction_Failure_WhenWithdrawingInvalidAmount() throws Exception {
        double amount = 500.0;
        Currency currency = Currency.USD;
        IntraTransactionDto transactionDto = new IntraTransactionDto(TransactionType.WITHDRAWAL, amount, currency);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new InsufficientFundsException("Insufficient balance")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTRA_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));

        assertThrows(InsufficientFundsException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransaction_Success_WhenTransferringValidAmount() throws Exception {
        double amount = 100.0;
        Long recipientId = 2L;
        InterTransactionDto transactionDto = new InterTransactionDto(amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doNothing().when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTER_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transaction successful"));
    }

    @Test
    void testCreateTransaction_Failure_WhenTransferAmountGreaterThanCurrentBalance() throws Exception {
        double amount = 100.0;
        Long recipientId = 2L;
        InterTransactionDto transactionDto = new InterTransactionDto(amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new InsufficientFundsException("Transfer amount should be less than current balance")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTER_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Transfer amount should be less than current balance"));

        assertThrows(InsufficientFundsException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransaction_Failure_WhenTransferringAmountToUnregisteredRecipientWallet() throws Exception {
        double amount = 100.0;
        Long recipientId = 2L;
        InterTransactionDto transactionDto = new InterTransactionDto(amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UserNotFoundException("Amount can't be transferred to unregistered recipient wallet")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTER_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount can't be transferred to unregistered recipient wallet"));

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransaction_InterTransaction_ThrowsWalletNotFoundException() throws Exception {
        double amount = 100.0;
        Long recipientId = 1L;
        InterTransactionDto transactionDto = new InterTransactionDto(amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new WalletNotFoundException("Wallet not found")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTER_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Wallet not found"));

        assertThrows(WalletNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransaction_InterTransaction_ThrowsUserNotFoundException() throws Exception {
        double amount = 100.0;
        Long recipientId = 1L;
        InterTransactionDto transactionDto = new InterTransactionDto(amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UserNotFoundException("User not found")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTER_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testCreateTransaction_InterTransaction_ThrowsUnauthorizedUserException() throws Exception {
        double amount = 100.0;
        Long recipientId = 1L;
        InterTransactionDto transactionDto = new InterTransactionDto(amount, recipientId);
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UnauthorizedAccessException("Unauthorized access to wallet")).when(transactionService).createTransaction(userId, walletId, transactionDto);

        mockMvc.perform(post(INTER_TRANSACTIONS_URL, userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unauthorized access to wallet"));

        assertThrows(UnauthorizedAccessException.class, () -> transactionService.createTransaction(userId, walletId, transactionDto));
    }

    @Test
    void testGetTransactions_ThrowsWalletNotFoundException() throws Exception {
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new WalletNotFoundException("Wallet not found")).when(transactionService).getTransactions(userId, walletId);

        mockMvc.perform(get(FETCH_TRANSACTIONS_URL, userId, walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThrows(WalletNotFoundException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    void testGetTransactions_ThrowsUserNotFoundException() throws Exception {
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UserNotFoundException("User not found")).when(transactionService).getTransactions(userId, walletId);

        mockMvc.perform(get(FETCH_TRANSACTIONS_URL, userId, walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThrows(UserNotFoundException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    void testGetTransactions_ThrowsUnauthorizedUserException() throws Exception {
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new UnauthorizedAccessException("Unauthorized access to wallet")).when(transactionService).getTransactions(userId, walletId);

        mockMvc.perform(get(FETCH_TRANSACTIONS_URL, userId, walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThrows(UnauthorizedAccessException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    void testGetTransactions_ThrowsNoTransactionsFoundException() throws Exception {
        Long userId = 1L;
        Long walletId = 1L;

        doThrow(new NoTransactionsFoundException("No transactions found for user")).when(transactionService).getTransactions(userId, walletId);

        mockMvc.perform(get(FETCH_TRANSACTIONS_URL, userId, walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        assertThrows(NoTransactionsFoundException.class, () -> transactionService.getTransactions(userId, walletId));
    }

    @Test
    void testGetTransactions_SuccessfullyFetchListOfTransactions() throws Exception {
        Long userId = 1L;
        Long walletId = 1L;

        TransactionDto firstTransactionDto = new TransactionDto(1L, 100.0, TransactionType.DEPOSIT, userId);
        TransactionDto secondTransactionDto = new TransactionDto(2L, 50.0, TransactionType.WITHDRAWAL, userId);
        when(transactionService.getTransactions(userId, walletId)).thenReturn(List.of(firstTransactionDto, secondTransactionDto));

        MvcResult mvcResult = mockMvc.perform(get(FETCH_TRANSACTIONS_URL, userId, walletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String expectedResponse = objectMapper.writeValueAsString(List.of(firstTransactionDto, secondTransactionDto));
        String actualResponse = mvcResult.getResponse().getContentAsString();

        JSONAssert.assertEquals(expectedResponse, actualResponse, false);
    }
}