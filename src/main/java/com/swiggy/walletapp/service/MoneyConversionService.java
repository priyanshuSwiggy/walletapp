package com.swiggy.walletapp.service;

import com.swiggy.walletapp.dto.MoneyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pb.MoneyConverterGrpc;
import pb.MoneyConverterOuterClass;

@Service
@RequiredArgsConstructor
public class MoneyConversionService {

    private final MoneyConverterGrpc.MoneyConverterBlockingStub moneyConverterStub;

    public MoneyDto convertMoney(MoneyDto money, String toCurrency) {
        MoneyConverterOuterClass.Money fromMoney = MoneyConverterOuterClass.Money.newBuilder()
                .setCurrency(money.getCurrency())
                .setAmount(money.getAmount())
                .build();

        MoneyConverterOuterClass.ConvertRequest request = MoneyConverterOuterClass.ConvertRequest.newBuilder()
                .setFrom(fromMoney)
                .setToCurrency(toCurrency)
                .build();

        MoneyConverterOuterClass.ConvertResponse response = moneyConverterStub.convert(request);
        return new MoneyDto(response.getConverted().getCurrency(), response.getConverted().getAmount());
    }
}
