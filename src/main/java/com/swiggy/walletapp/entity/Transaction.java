package com.swiggy.walletapp.entity;

import com.swiggy.walletapp.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "transaction")
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private Long userId;

    public Transaction(double amount, TransactionType transactionType, Long userId) {
        this.amount = amount;
        this.transactionType = transactionType;
        this.userId = userId;
    }
}
