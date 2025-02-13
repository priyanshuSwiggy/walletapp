package com.swiggy.walletapp.service;

import com.swiggy.walletapp.entity.Client;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.exception.InvalidCredentialsException;
import com.swiggy.walletapp.exception.UserAlreadyExistsException;
import com.swiggy.walletapp.repository.ClientRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    private ClientRepository clientRepository;
    private WalletRepository walletRepository;
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        walletRepository = mock(WalletRepository.class);
        clientService = new ClientService(clientRepository, walletRepository);
    }

    @Test
    public void registerClient_UserAlreadyExists_ThrowsException() {
        Client existingClient = new Client("username", "password");
        when(clientRepository.findAll()).thenReturn(List.of(existingClient));

        assertThrows(UserAlreadyExistsException.class, () -> clientService.register(existingClient));
    }

    @Test
    public void registerClient_NewUser_SuccessfullyRegisters() {
        Client newClient = new Client("newUsername", "newPassword");
        when(clientRepository.findAll()).thenReturn(emptyList());

        clientService.register(newClient);

        verify(clientRepository).save(newClient);
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    public void login_ValidCredentials_ReturnsClient() {
        Client client = new Client("username", "password");
        when(clientRepository.findByUsername("username")).thenReturn(Optional.of(client));

        Client loggedInClient = clientService.login("username", "password");

        assertEquals(client, loggedInClient);
    }

    @Test
    public void login_InvalidCredentials_ThrowsException() {
        when(clientRepository.findByUsername("username")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> clientService.login("username", "password"));
    }
}
