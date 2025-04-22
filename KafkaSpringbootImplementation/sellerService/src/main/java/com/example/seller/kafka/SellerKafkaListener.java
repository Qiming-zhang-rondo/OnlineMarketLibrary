package com.example.seller.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SellerKafkaListener {

    private final JsonSellerConsumer jsonSellerConsumer;

    public SellerKafkaListener(JsonSellerConsumer jsonSellerConsumer) {
        this.jsonSellerConsumer = jsonSellerConsumer;
    }

    @KafkaListener(topics = "invoice-issued-topic", groupId = "seller-group")
    public void listenInvoiceIssued(String message) {
        jsonSellerConsumer.handleInvoiceIssued(message);
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "seller-group")
    public void listenPaymentFailed(String message) {
        jsonSellerConsumer.handlePaymentFailed(message);
    }

    @KafkaListener(topics = "shipment-notification-topic", groupId = "seller-group")
    public void listenShipmentNotification(String message) {
        jsonSellerConsumer.handleShipmentNotification(message);
    }

    @KafkaListener(topics = "delivery-notification-topic", groupId = "seller-group")
    public void listenDeliveryNotification(String message) {
        jsonSellerConsumer.handleDeliveryNotification(message);
    }

    @KafkaListener(topics = "payment-confirmed-topic", groupId = "seller-group")
    public void listenPaymentConfirmed(String message) {
        jsonSellerConsumer.handlePaymentConfirmed(message);
    }
}

