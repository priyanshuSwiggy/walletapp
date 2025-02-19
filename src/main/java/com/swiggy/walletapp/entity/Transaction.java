package com.swiggy.walletapp.entity;

import com.swiggy.walletapp.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;
    private TransactionType transactionType;
    private Long senderId;

    public Transaction(double convertedAmount, TransactionType transactionType, Long userId) {
        this.amount = convertedAmount;
        this.transactionType = transactionType;
        this.senderId = userId;
    }

    public Transaction() {

    }
}
