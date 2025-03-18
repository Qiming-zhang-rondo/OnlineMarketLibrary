package com.example.paymentProvider;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.paymentProvider.infra.PaymentProviderConfig;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties(PaymentProviderConfig.class)
public class PaymentProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentProviderApplication.class, args);
    }
}