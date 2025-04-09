package com.example.paymentProvider;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.paymentProvider.config.PaymentProviderConfig;

@SpringBootApplication
@EnableConfigurationProperties(PaymentProviderConfig.class)
public class PaymentProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentProviderApplication.class, args);
    }
}