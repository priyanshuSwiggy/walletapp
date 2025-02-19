package com.swiggy.walletapp.config;

import com.swiggy.walletapp.mapper.TransactionMapper;
import com.swiggy.walletapp.mapper.TransactionMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionConfig {

    @Bean
    public TransactionMapper transactionMapper() {
        return new TransactionMapperImpl();
    }
}
