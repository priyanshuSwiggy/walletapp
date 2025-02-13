package com.swiggy.walletapp.controller;

import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.InsufficientFundsException;
import com.swiggy.walletapp.exception.InvalidAmountException;
import com.swiggy.walletapp.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalletControllerTest {

    public static final String WALLET_DEPOSIT_URL = "/wallets/{username}/deposit";
    public static final String WALLET_WITHDRAW_URL = "/wallets/{username}/withdraw";
    public static final String REQ_PARAM_AMOUNT = "amount";
    public static final String REQ_PARAM_CURRENCY = "currency";
    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
    }

    @Test
    void deposit_Successfully_WhenDepositingValidAmount() throws Exception {
        String username = "testUser";
        double amount = 100.0;
        Currency currency = Currency.INR;

        doNothing().when(walletService).deposit(username, amount, currency);

        mockMvc.perform(post(WALLET_DEPOSIT_URL, username)
                        .param(REQ_PARAM_AMOUNT, String.valueOf(amount))
                        .param(REQ_PARAM_CURRENCY, currency.name()))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit successful"));
    }

    @Test
    void deposit_Failure_WhenDepositingInvalidAmount() throws Exception {
        String username = "testUser";
        double amount = -100.0;
        Currency currency = Currency.INR;

        doThrow(new InvalidAmountException("Deposit amount must be positive")).when(walletService).deposit(username, amount, currency);

        mockMvc.perform(post(WALLET_DEPOSIT_URL, username)
                        .param(REQ_PARAM_AMOUNT, String.valueOf(amount))
                        .param(REQ_PARAM_CURRENCY, currency.name()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Deposit amount must be positive"));

        assertThrows(InvalidAmountException.class, () -> walletService.deposit(username, amount, currency));
    }

    @Test
    void withdraw_Success_WhenWithdrawingValidAmount() throws Exception {
        String username = "testUser";
        double amount = 50.0;
        Currency currency = Currency.USD;

        doNothing().when(walletService).withdraw(username, amount, currency);

        mockMvc.perform(post(WALLET_WITHDRAW_URL, username)
                        .param(REQ_PARAM_AMOUNT, String.valueOf(amount))
                        .param(REQ_PARAM_CURRENCY, currency.name()))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdrawal successful"));
    }

    @Test
    void withdraw_Failure_WhenWithdrawingInvalidAmount() throws Exception {
        String username = "testUser";
        double amount = 500.0;
        Currency currency = Currency.USD;

        doThrow(new InsufficientFundsException("Insufficient balance")).when(walletService).withdraw(username, amount, currency);

        mockMvc.perform(post(WALLET_WITHDRAW_URL, username)
                        .param(REQ_PARAM_AMOUNT, String.valueOf(amount))
                        .param(REQ_PARAM_CURRENCY, currency.name()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));

        assertThrows(InsufficientFundsException.class, () -> walletService.withdraw(username, amount, currency));
    }
}