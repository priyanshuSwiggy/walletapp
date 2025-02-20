package com.swiggy.walletapp.entity;

import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@Table(name = "inter-transaction")
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class InterTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private Long senderId;
    private Long recipientId;

    private LocalDateTime timestamp;

    public InterTransaction(double amount, Currency currency, TransactionType transactionType, Long senderId, Long recipientId) {
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
