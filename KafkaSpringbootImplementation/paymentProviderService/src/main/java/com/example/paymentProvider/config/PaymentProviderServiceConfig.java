package com.example.paymentProvider.config;

import com.example.paymentProvider.service.PaymentProviderCore;
import com.example.paymentProvider.service.IPaymentProvider;
import com.example.paymentProvider.config.PaymentProviderConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentProviderServiceConfig {

    @Bean
    public IPaymentProvider paymentProviderCore(PaymentProviderConfig config) {
        return new PaymentProviderCore(config);
    }
}