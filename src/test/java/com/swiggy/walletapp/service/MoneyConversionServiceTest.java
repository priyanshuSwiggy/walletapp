package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.MoneyDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pb.MoneyConverterGrpc;
import pb.MoneyConverterOuterClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoneyConversionServiceTest {

    private MoneyConverterGrpc.MoneyConverterBlockingStub moneyConverterStub;
    private MoneyConversionService moneyConversionService;

    @BeforeEach
    void setUp() {
        moneyConverterStub = mock(MoneyConverterGrpc.MoneyConverterBlockingStub.class);
        moneyConversionService = new MoneyConversionService(moneyConverterStub);
    }

    @Test
    void convertMoneySuccessfully() {
        MoneyDto money = new MoneyDto("USD", 100.0);
        String toCurrency = "INR";
        MoneyConverterOuterClass.Money fromMoney = MoneyConverterOuterClass.Money.newBuilder()
                .setCurrency("USD")
                .setAmount(100.0)
                .build();
        MoneyConverterOuterClass.ConvertRequest request = MoneyConverterOuterClass.ConvertRequest.newBuilder()
                .setFrom(fromMoney)
                .setToCurrency(toCurrency)
                .build();
        MoneyConverterOuterClass.Money convertedMoney = MoneyConverterOuterClass.Money.newBuilder()
                .setCurrency("INR")
                .setAmount(7500.0)
                .build();
        MoneyConverterOuterClass.ConvertResponse response = MoneyConverterOuterClass.ConvertResponse.newBuilder()
                .setConverted(convertedMoney)
                .build();

        when(moneyConverterStub.convert(request)).thenReturn(response);

        MoneyDto result = moneyConversionService.convertMoney(money, toCurrency);

        assertEquals("INR", result.getCurrency());
        assertEquals(7500.0, result.getAmount());
    }
}