package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.UserDto;
import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.UserAlreadyExistsException;
import com.swiggy.walletapp.repository.UserRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(UserDto userDto) {
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        Currency defaultCurrency = userDto.getCurrency() == null ? Currency.INR : userDto.getCurrency();
        User user = new User(username, password);
        userRepository.findByUsername(username).ifPresent(u -> {
            throw new UserAlreadyExistsException("User already exists", HttpStatus.CONFLICT);
        });
        user.encodePassword(passwordEncoder);
        userRepository.save(user);
        Wallet wallet = new Wallet(user, defaultCurrency);
        walletRepository.save(wallet);
    }
}
