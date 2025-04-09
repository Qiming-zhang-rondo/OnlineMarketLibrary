package com.example.payment.config;

import com.example.common.messaging.IEventPublisher;
import com.example.payment.kafka.JsonPaymentConsumer;
import com.example.payment.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import com.example.payment.repository.IPaymentRepository;
import com.example.payment.repository.IOrderPaymentCardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentServiceConfig {

    @Bean
    public IPaymentService paymentService(
            IPaymentRepository paymentRepository,
            IOrderPaymentCardRepository cardRepository,
            IEventPublisher eventPublisher,
            IPaymentConfig config,
            IExternalProvider externalProvider
    ) {
        return new PaymentServiceCore(paymentRepository, cardRepository, eventPublisher, config, externalProvider);
    }

    @Bean
    public JsonPaymentConsumer jsonPaymentConsumer(IPaymentService paymentService, ObjectMapper objectMapper) {
        return new JsonPaymentConsumer(paymentService, objectMapper);
    }

    @Bean
    public IExternalProvider externalProvider() {
        return new ExternalProviderProxyCore(10);
    }


}