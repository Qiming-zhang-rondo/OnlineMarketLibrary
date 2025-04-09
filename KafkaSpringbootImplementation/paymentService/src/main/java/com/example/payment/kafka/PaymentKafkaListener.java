package com.example.payment.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentKafkaListener {

    private final JsonPaymentConsumer jsonPaymentConsumer;

    public PaymentKafkaListener(JsonPaymentConsumer jsonPaymentConsumer) {
        this.jsonPaymentConsumer = jsonPaymentConsumer;
    }

    @KafkaListener(topics = "invoice-issued-topic", groupId = "payment-group")
    public void listenInvoiceIssued(String payload) {
        jsonPaymentConsumer.handleInvoiceIssued(payload);
    }
}