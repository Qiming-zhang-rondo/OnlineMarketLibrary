package com.example.customer.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CustomerKafkaListener {

    private final JsonCustomerConsumer customerConsumer;

    public CustomerKafkaListener(JsonCustomerConsumer customerConsumer) {
        this.customerConsumer = customerConsumer;
    }

    @KafkaListener(topics = "delivery-notification-topic", groupId = "customer-group")
    public void listenDeliveryNotification(String payload) {
        customerConsumer.handleDeliveryNotification(payload);
    }

    @KafkaListener(topics = "payment-confirmed-topic", groupId = "customer-group")
    public void listenPaymentConfirmed(String payload) {
        customerConsumer.handlePaymentConfirmed(payload);
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "customer-group")
    public void listenPaymentFailed(String payload) {
        customerConsumer.handlePaymentFailed(payload);
    }
}