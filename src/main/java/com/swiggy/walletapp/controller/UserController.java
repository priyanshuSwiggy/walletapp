package com.swiggy.walletapp.controller;

import com.swiggy.walletapp.dto.UserDto;
import com.swiggy.walletapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> registerClient(@RequestBody UserDto userDto) {
        userService.register(userDto);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }
}
