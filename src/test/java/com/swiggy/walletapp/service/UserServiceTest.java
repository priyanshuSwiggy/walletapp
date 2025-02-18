package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.UserDto;
import com.swiggy.walletapp.entity.User;
import com.swiggy.walletapp.entity.Wallet;
import com.swiggy.walletapp.enums.Currency;
import com.swiggy.walletapp.exception.UserAlreadyExistsException;
import com.swiggy.walletapp.repository.UserRepository;
import com.swiggy.walletapp.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;

    private WalletRepository walletRepository;

    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        walletRepository = mock(WalletRepository.class);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, walletRepository, passwordEncoder);
    }

    @Test
    public void testRegister_NewUser_SuccessfullyRegisters() {
        UserDto userDto = new UserDto("test", "test");
        User user = new User("test", "test");
        Wallet wallet = new Wallet(user, Currency.INR);
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

        userService.register(userDto);
        verify(userRepository, times(1)).findByUsername("test");
        verify(userRepository, times(1)).save(user);
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    public void testRegisterUser_UserAlreadyExists_ThrowsException() {
        UserDto userDto = new UserDto("test", "test");
        User user = new User("test", "test");
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        assertThrows(UserAlreadyExistsException.class, () -> userService.register(userDto));
        verify(userRepository, times(1)).findByUsername("test");
        verify(userRepository, never()).save(user);
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}
