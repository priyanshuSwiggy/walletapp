package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.MoneyConversionRequest;
import com.swiggy.walletapp.dto.MoneyConversionResponse;
import com.swiggy.walletapp.dto.MoneyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pb.MoneyConverterGrpc;
import pb.MoneyConverterOuterClass;

@Service
@RequiredArgsConstructor
public class MoneyConversionService {

//    private final MoneyConverterGrpc.MoneyConverterBlockingStub moneyConverterStub;
//
//    public MoneyDto convertMoney(MoneyDto money, String toCurrency) {
//        MoneyConverterOuterClass.Money fromMoney = MoneyConverterOuterClass.Money.newBuilder()
//                .setCurrency(money.getCurrency())
//                .setAmount(money.getAmount())
//                .build();
//
//        MoneyConverterOuterClass.ConvertRequest request = MoneyConverterOuterClass.ConvertRequest.newBuilder()
//                .setFrom(fromMoney)
//                .setToCurrency(toCurrency)
//                .build();
//
//        MoneyConverterOuterClass.ConvertResponse response = moneyConverterStub.convert(request);
//        return new MoneyDto(response.getConverted().getCurrency(), response.getConverted().getAmount());
//    }

    private final RestTemplate restTemplate;

    private static final String CONVERSION_URL = "http://localhost:8085/convert";

    public MoneyDto convertMoney(MoneyDto money, String toCurrency) {
        MoneyConversionRequest request = new MoneyConversionRequest(money, toCurrency);

        ResponseEntity<MoneyConversionResponse> response = restTemplate.postForEntity(
                CONVERSION_URL, request, MoneyConversionResponse.class
        );

        MoneyConversionResponse responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("Invalid response from currency conversion service");
        }

        return new MoneyDto(responseBody.getConverted().getCurrency(), responseBody.getConverted().getAmount());
    }
}
