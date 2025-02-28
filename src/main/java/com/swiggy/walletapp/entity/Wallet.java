package com.swiggy.walletapp.entity;

import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.InsufficientFundsException;
import com.swiggy.walletapp.exception.InvalidAmountException;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "wallet")
@RequiredArgsConstructor
@EqualsAndHashCode
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private double balance;

    @Getter
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Getter
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Wallet(User user, Currency currency) {
        this.balance = 0;
        this.user = user;
        this.currency = currency;
    }

    public Wallet(double balance, User user, Currency currency) {
        this.balance = balance;
        this.user = user;
        this.currency = currency;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive", HttpStatus.BAD_REQUEST);
        }
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive", HttpStatus.BAD_REQUEST);
        }
        if (this.balance < amount) {
            throw new InsufficientFundsException("Insufficient funds", HttpStatus.BAD_REQUEST);
        }
        this.balance -= amount;
    }

    public boolean checkBalance(double balance) {
        return this.balance == balance;
    }

    public boolean isOwnedBy(User user) {
        return this.user.equals(user);
    }
}
