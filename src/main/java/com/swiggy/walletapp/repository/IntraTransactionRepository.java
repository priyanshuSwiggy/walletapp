package com.swiggy.walletapp.repository;

import com.swiggy.walletapp.entity.IntraTransaction;
import com.swiggy.walletapp.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntraTransactionRepository extends JpaRepository<IntraTransaction, Long> {
    List<IntraTransaction> findByUserId(Long userId);
    List<IntraTransaction> findByUserIdAndTransactionType(Long userId, TransactionType transactionType);
}