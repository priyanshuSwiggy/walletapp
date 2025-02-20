package com.swiggy.walletapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title="wallet-service",
                version = "1.0.0",
                contact = @Contact(
                        name = "Kumar Priyanshu",
                        email = "kumar.priyanshu_ftc@external.swiggy.in"),
                license = @License(
                        name = "Terms of Service",
                        url = "")),
        servers= @Server(url = "http://localhost:8081")
)
public class SwaggerConfig {

}
