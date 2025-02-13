package com.swiggy.walletapp.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

@Entity
@EqualsAndHashCode(callSuper = false)
public class Client extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private Wallet wallet;

    public Client(String username, String password) {
        super(username, password, Collections.emptyList());
        this.password = password;
    }

    public Client(String username, String password, Wallet wallet) {
        super(username, password, Collections.emptyList());
        this.password = password;
        this.wallet = wallet;
    }

    public Client() {
        super("defaultUsername", "defaultPassword", Collections.emptyList());
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}
