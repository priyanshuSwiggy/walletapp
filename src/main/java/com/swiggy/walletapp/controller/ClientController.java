package com.swiggy.walletapp.controller;

import com.swiggy.walletapp.entity.Client;
import com.swiggy.walletapp.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<String> registerClient(@RequestBody Client client) {
        try {
            clientService.register(client);
            return new ResponseEntity<>("Client registered successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Client> loginClient(@RequestParam String username, @RequestParam String password) {
        try {
            Client client = clientService.login(username, password);
            return new ResponseEntity<>(client, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}
