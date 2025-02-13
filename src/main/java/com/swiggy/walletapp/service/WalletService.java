package com.swiggy.walletapp.service;

import com.swiggy.walletapp.entity.Client;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.UnauthorizedAccessException;
import com.swiggy.walletapp.exception.WalletNotFoundException;
import com.swiggy.walletapp.repository.ClientRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final ClientRepository clientRepository;
    private final WalletRepository walletRepository;

    public void deposit(String username, double amount, Currency currency) {
        Wallet wallet = fetchWalletOfClient(username);
        double amountInINR = currency.convertToINR(amount);
        wallet.deposit(amountInINR);
        walletRepository.save(wallet);
    }

    public void withdraw(String username, double amount, Currency currency) {
        Wallet wallet = fetchWalletOfClient(username);
        double amountInINR = currency.convertToINR(amount);
        wallet.withdraw(amountInINR);
        walletRepository.save(wallet);
    }

    private Wallet fetchWalletOfClient(String username) {
        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new WalletNotFoundException("Client not found"));
        Wallet wallet = walletRepository.findByClient(client)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for client"));
        if (!wallet.isOwnedBy(client)) {
            throw new UnauthorizedAccessException("Unauthorized access to wallet");
        }
        return wallet;
    }
}