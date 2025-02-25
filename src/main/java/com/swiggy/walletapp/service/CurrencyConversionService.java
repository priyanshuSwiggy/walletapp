package com.swiggy.walletapp.service;

import io.grpc.ManagedChannel;
import org.springframework.stereotype.Service;
import pb.CurrencyConverterGrpc;
import pb.CurrencyConverterOuterClass;

@Service
public class CurrencyConversionService {

    private final CurrencyConverterGrpc.CurrencyConverterBlockingStub currencyConverterStub;

    public CurrencyConversionService(ManagedChannel grpcChannel) {
        this.currencyConverterStub = CurrencyConverterGrpc.newBlockingStub(grpcChannel);
    }

    public double convertCurrency(String fromCurrency, String toCurrency, double amount) {
        CurrencyConverterOuterClass.ConvertRequest request = CurrencyConverterOuterClass.ConvertRequest.newBuilder()
                .setFromCurrency(fromCurrency)
                .setToCurrency(toCurrency)
                .setAmount(amount)
                .build();

        CurrencyConverterOuterClass.ConvertResponse response = currencyConverterStub.convert(request);
        return response.getConvertedAmount();
    }
}
