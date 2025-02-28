package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.MoneyConversionRequest;
import com.swiggy.walletapp.dto.MoneyConversionResponse;
import com.swiggy.walletapp.dto.MoneyDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoneyConversionServiceTest {

    private static final String CONVERSION_URL = "http://localhost:8085/convert";

    private RestTemplate restTemplate;
    private MoneyConversionService moneyConversionService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        moneyConversionService = new MoneyConversionService(restTemplate);
    }

    @Test
    void convertMoneySuccessfully() {
        MoneyDto money = new MoneyDto("USD", 100.0);
        String toCurrency = "INR";
        MoneyConversionRequest request = new MoneyConversionRequest(money, toCurrency);
        MoneyConversionResponse response = new MoneyConversionResponse(new MoneyDto("INR", 7500.0));
        when(restTemplate.postForEntity(CONVERSION_URL, request, MoneyConversionResponse.class))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        MoneyDto result = moneyConversionService.convertMoney(money, toCurrency);

        assertEquals("INR", result.getCurrency());
        assertEquals(7500.0, result.getAmount());
    }

    @Test
    void convertMoneyThrowsExceptionWhenResponseIsNull() {
        MoneyDto money = new MoneyDto("USD", 100.0);
        String toCurrency = "INR";
        MoneyConversionRequest request = new MoneyConversionRequest(money, toCurrency);
        when(restTemplate.postForEntity(CONVERSION_URL, request, MoneyConversionResponse.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThrows(RuntimeException.class, () -> moneyConversionService.convertMoney(money, toCurrency));
    }

    @Test
    void convertMoneyThrowsExceptionWhenResponseBodyIsNull() {
        MoneyDto money = new MoneyDto("USD", 100.0);
        String toCurrency = "INR";
        MoneyConversionRequest request = new MoneyConversionRequest(money, toCurrency);
        when(restTemplate.postForEntity(CONVERSION_URL, request, MoneyConversionResponse.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        assertThrows(RuntimeException.class, () -> moneyConversionService.convertMoney(money, toCurrency));
    }
}