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

import java.math.BigDecimal;

@Entity
@Table(name = "wallet")
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
        double convertedAmount = convertedAmount(this.currency, amount);
        if (convertedAmount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive", HttpStatus.BAD_REQUEST);
        }
        this.balance += convertedAmount;
    }

    public void withdraw(double amount) {
        double convertedAmount = convertedAmount(this.currency, amount);
        if (convertedAmount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive", HttpStatus.BAD_REQUEST);
        }
        if (this.balance < convertedAmount) {
            throw new InsufficientFundsException("Insufficient funds", HttpStatus.BAD_REQUEST);
        }
        this.balance -= convertedAmount;
    }

    public boolean checkBalance(double balance) {
        return this.balance == balance;
    }

    public boolean isOwnedBy(User user) {
        return this.user.equals(user);
    }

    public double convertedAmount(Currency currency, double amount) {
        return currency.convertTo(this.currency, amount);
    }
}
