package com.swiggy.walletapp.service;

import com.swiggy.walletapp.entity.Client;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.InvalidCredentialsException;
import com.swiggy.walletapp.exception.UserAlreadyExistsException;
import com.swiggy.walletapp.repository.ClientRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final WalletRepository walletRepository;
//    private final Function<String, String> passwordEncoder = new User.UserBuilder()::encode;

    public void register(Client client) {
        List<Client> clients = clientRepository.findAll();
        for(Client c : clients) {
            if(c == client) {
                throw new UserAlreadyExistsException("User already exists");
            }
        }
        clientRepository.save(client);
        Wallet wallet = new Wallet(client, Currency.INR);
        walletRepository.save(wallet);
    }

    public Client login(String username, String password) {
        return clientRepository.findByUsername(username)
                .filter(client -> client.checkPassword(password))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password."));
    }
}
