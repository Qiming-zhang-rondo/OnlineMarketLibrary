package com.example.shipment.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentKafkaListener {

    private final JsonShipmentConsumer jsonShipmentConsumer;

    public ShipmentKafkaListener(JsonShipmentConsumer jsonShipmentConsumer) {
        this.jsonShipmentConsumer = jsonShipmentConsumer;
    }

    @KafkaListener(topics = "payment-confirmed-topic", groupId = "shipment-group")
    public void listenPaymentConfirmed(String message) {
        jsonShipmentConsumer.handlePaymentConfirmed(message);
    }
}
