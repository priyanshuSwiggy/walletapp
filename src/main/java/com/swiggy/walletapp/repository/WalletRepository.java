package com.swiggy.walletapp.repository;

import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);

    List<Wallet> findAllByUser(User user);
}