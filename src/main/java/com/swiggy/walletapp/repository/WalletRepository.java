package com.swiggy.walletapp.repository;

import com.swiggy.walletapp.entity.Client;
import com.swiggy.walletapp.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByClient(Client client);
}