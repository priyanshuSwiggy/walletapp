package com.swiggy.walletapp.entity;

import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@Table(name = "intra-transaction")
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class IntraTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private Long userId;

    private LocalDateTime timestamp;

    public IntraTransaction(double amount, Currency currency, TransactionType transactionType, Long userId) {
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.userId = userId;
    }

    public IntraTransaction(double amount, TransactionType transactionType, Long userId) {
        this.amount = amount;
        this.transactionType = transactionType;
        this.userId = userId;
    }

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
