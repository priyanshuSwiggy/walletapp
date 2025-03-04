package com.swiggy.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiggy.walletapp.dto.WalletRequestDto;
import com.swiggy.walletapp.dto.WalletResponseDto;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.GlobalExceptionHandler;
import com.swiggy.walletapp.exception.UserNotFoundException;
import com.swiggy.walletapp.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(MockitoExtension.class)
public class WalletControllerTest {

    private static final String WALLET_URL = "/users/{userId}/wallets";

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private WalletRequestDto walletRequestDto;

    @BeforeEach
    void setUp() {
        walletRequestDto = new WalletRequestDto(Currency.INR);
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    public void testCreateWalletSuccessfullyWhenValidInput() throws Exception {
        Long userId = 1L;
        doNothing().when(walletService).createWallet(userId, walletRequestDto);

        mockMvc.perform(post(WALLET_URL, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Wallet created successfully"));
    }

    @Test
    public void createWalletFailureWhenInvalidUserId() throws Exception {
        doThrow(new UserNotFoundException("User not found", HttpStatus.NOT_FOUND)).when(walletService).createWallet(999L, walletRequestDto);

        mockMvc.perform(post(WALLET_URL, 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    public void getWalletsSuccessfullyWhenUserExists() throws Exception {
        Long userId = 1L;
        List<WalletResponseDto> walletResponseDtos = List.of(
                new WalletResponseDto(1L, 1000.0, Currency.INR),
                new WalletResponseDto(2L, 2000.0, Currency.USD)
        );
        when(walletService.getWallets(userId)).thenReturn(walletResponseDtos);

        mockMvc.perform(get(WALLET_URL, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(walletResponseDtos)));
    }

    @Test
    public void getWalletsFailureWhenUserNotFound() throws Exception {
        Long userId = 999L;
        when(walletService.getWallets(userId)).thenThrow(new UserNotFoundException("User not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get(WALLET_URL, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }
}
