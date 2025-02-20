package com.swiggy.walletapp.repository;

import com.swiggy.walletapp.entity.InterTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterTransactionRepository extends JpaRepository<InterTransaction, Long> {
    List<InterTransaction> findByRecipientId(Long userId);
    List<InterTransaction> findBySenderId(Long userId);
}