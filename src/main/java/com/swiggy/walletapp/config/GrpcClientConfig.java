package com.swiggy.walletapp.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pb.MoneyConverterGrpc;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
    }

    @Bean
    public MoneyConverterGrpc.MoneyConverterBlockingStub moneyConverterStub(ManagedChannel grpcChannel) {
        return MoneyConverterGrpc.newBlockingStub(grpcChannel);
    }
}
