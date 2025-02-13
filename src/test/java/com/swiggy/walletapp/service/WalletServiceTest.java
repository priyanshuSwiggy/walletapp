package com.swiggy.walletapp.service;

import com.swiggy.walletapp.entity.Client;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.InsufficientFundsException;
import com.swiggy.walletapp.exception.InvalidAmountException;
import com.swiggy.walletapp.exception.UnauthorizedAccessException;
import com.swiggy.walletapp.exception.WalletNotFoundException;
import com.swiggy.walletapp.repository.ClientRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class WalletServiceTest {

    private ClientRepository clientRepository;
    private WalletRepository walletRepository;
    private WalletService walletService;


    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        walletRepository = mock(WalletRepository.class);
        walletService = new WalletService(clientRepository, walletRepository);
    }

    @Test
    public void deposit_ValidClient_UpdatesBalance() {
        Client client = new Client("username", "password");
        Wallet wallet = new Wallet(client, Currency.INR);
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(walletRepository.findByClient(client)).thenReturn(Optional.of(wallet));

        walletService.deposit("username", 100.0, Currency.USD);

        double expectedBalance = 100.0 * Currency.USD.getConversionRate();
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void deposit_ValidClientEUR_UpdatesBalance() {
        Client client = new Client("username", "password");
        Wallet wallet = new Wallet(client, Currency.INR);
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(walletRepository.findByClient(client)).thenReturn(Optional.of(wallet));

        walletService.deposit("username", 100.0, Currency.EUR);

        double expectedBalance = 100.0 * Currency.EUR.getConversionRate();
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void deposit_WalletNotFoundException_ThrowsException() {
        Client client = new Client("username", "password");
        when(walletRepository.findByClient(client)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.deposit("username", 100.0, Currency.USD));
    }

    @Test
    public void deposit_NegativeAmount_ThrowsException() {
        Client client = new Client("username", "password");
        Wallet wallet = new Wallet(client, Currency.INR);
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(walletRepository.findByClient(client)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> walletService.deposit("username", -100.0, Currency.USD));
    }

    @Test
    public void deposit_ZeroAmount_ThrowsException() {
        Client client = new Client("username", "password");
        Wallet wallet = new Wallet(client, Currency.INR);
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(walletRepository.findByClient(client)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> walletService.deposit("username", 0.0, Currency.USD));
    }

    @Test
    public void deposit_UnauthorizedClient_ThrowsException() {
        Client client = new Client("username", "password");
        Client otherClient = new Client("otherUsername", "password");
        Wallet wallet = mock(Wallet.class);
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(walletRepository.findByClient(client)).thenReturn(Optional.of(wallet));
        when(wallet.isOwnedBy(client)).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () -> walletService.deposit("username", 100.0, Currency.USD));
    }

    @Test
    public void withdraw_ValidClient_UpdatesBalance() {
        Client client = new Client("username", "password");
        Wallet wallet = new Wallet(client, Currency.INR);
        wallet.deposit(200.0 * Currency.USD.getConversionRate());
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(walletRepository.findByClient(client)).thenReturn(Optional.of(wallet));

        walletService.withdraw("username", 100.0, Currency.USD);

        double expectedBalance = 100.0 * Currency.USD.getConversionRate();
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void withdraw_ValidClientEUR_UpdatesBalance() {
        Client client = new Client("username", "password");
        Wallet wallet = new Wallet(client, Currency.INR);
        wallet.deposit(200.0 * Currency.EUR.getConversionRate());
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(walletRepository.findByClient(client)).thenReturn(Optional.of(wallet));

        walletService.withdraw("username", 100.0, Currency.EUR);

        double expectedBalance = 100.0 * Currency.EUR.getConversionRate();
        assertTrue(wallet.checkBalance(expectedBalance));
        verify(walletRepository).save(wallet);
    }

    @Test
    public void withdraw_WalletNotFoundException_ThrowsException() {
        Client client = new Client("username", "password");
        when(walletRepository.findByClient(client)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.withdraw("username", 100.0, Currency.USD));
    }

    @Test
    public void withdraw_NegativeAmount_ThrowsException() {
        Client client = new Client("username", "password");
        Wallet wallet = new Wallet(client, Currency.INR);
        wallet.deposit(200.0 * Currency.USD.getConversionRate());
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(walletRepository.findByClient(client)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> walletService.withdraw("username", -100.0, Currency.USD));
    }

    @Test
    public void withdraw_ZeroAmount_ThrowsException() {
        Client client = new Client("username", "password");
        Wallet wallet = new Wallet(client, Currency.INR);
        wallet.deposit(200.0 * Currency.USD.getConversionRate());
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(walletRepository.findByClient(client)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> walletService.withdraw("username", 0.0, Currency.USD));
    }

    @Test
    public void withdraw_InsufficientBalance_ThrowsException() {
        Client client = new Client("username", "password");
        Wallet wallet = new Wallet(client, Currency.INR);
        wallet.deposit(50.0 * Currency.USD.getConversionRate());
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(walletRepository.findByClient(client)).thenReturn(Optional.of(wallet));

        assertThrows(InsufficientFundsException.class, () -> walletService.withdraw("username", 100.0, Currency.USD));
    }

    @Test
    public void withdraw_UnauthorizedClient_ThrowsException() {
        Client client = new Client("username", "password");
        Client otherClient = new Client("otherUsername", "password");
        Wallet wallet = mock(Wallet.class);
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(walletRepository.findByClient(client)).thenReturn(Optional.of(wallet));
        when(wallet.isOwnedBy(client)).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () -> walletService.withdraw("username", 100.0, Currency.USD));
    }

}