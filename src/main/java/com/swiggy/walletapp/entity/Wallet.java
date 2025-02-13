package com.swiggy.walletapp.entity;

import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.InsufficientFundsException;
import com.swiggy.walletapp.exception.InvalidAmountException;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double balance;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false, referencedColumnName = "id")
    private Client client;

    public Wallet(Client client, Currency currency) {
        this.balance = 0;
        this.client = client;
        this.currency = currency;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive.");
        }
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive.");
        }
        if (this.balance < amount) {
            throw new InsufficientFundsException("Insufficient funds.");
        }
        this.balance -= amount;
    }

    public boolean checkBalance(double balance) {
        return this.balance == balance;
    }

    public boolean isOwnedBy(Client client) {
        return this.client.equals(client);
    }
}
