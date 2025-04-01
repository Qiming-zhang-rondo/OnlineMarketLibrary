package com.example.shipment.kafka;

import com.example.shipment.service.ShipmentService;
import com.example.common.events.PaymentConfirmed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentKafkaConsumer.class);
    private final ShipmentService shipmentService;

    public ShipmentKafkaConsumer(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

  
    @KafkaListener(topics = "payment-confirmed-topic", groupId = "shipment-group")
    public void processPaymentConfirmed(PaymentConfirmed paymentConfirmed) {
        try {
            logger.info("receive paymentConfirmed: {}", paymentConfirmed);
            shipmentService.processShipment(paymentConfirmed);
        } catch (Exception e) {
            logger.error("Error processing payment confirmation: {}", e.getMessage());
            shipmentService.processPoisonShipment(paymentConfirmed);
        }
    }

}
