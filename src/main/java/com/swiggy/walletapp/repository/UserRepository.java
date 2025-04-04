package com.swiggy.walletapp.repository;

import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User>  findByWallet(Wallet wallet);
}
