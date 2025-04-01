package com.example.shipment.kafka;

import com.example.common.driver.TransactionMark;
import com.example.common.events.DeliveryNotification;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.ShipmentNotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShipmentKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentKafkaProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String PAYMENT_CONFIRMED_TOPIC = "payment-confirmed-topic";
    private static final String DELIVERY_NOTIFICATION_TOPIC = "delivery-notification-topic";
    private static final String SHIPMENT_NOTIFICATION_TOPIC = "shipment-notification-topic";  


    public ShipmentKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentConfirmed(PaymentConfirmed paymentConfirmed) {
        kafkaTemplate.send(PAYMENT_CONFIRMED_TOPIC, paymentConfirmed);
        logger.info("Sent payment confirmed event to Kafka: {}", paymentConfirmed.getOrderId());
    }

    public void sendDeliveryNotification(DeliveryNotification deliveryNotification) {
        kafkaTemplate.send(DELIVERY_NOTIFICATION_TOPIC, deliveryNotification);
        logger.info("Sent delivery notification to Kafka for order ID: {}", deliveryNotification.getOrderId());
    }

    public void sendShipmentNotification(ShipmentNotification shipmentNotification) {
        logger.info("Sending ShipmentNotification: {}", shipmentNotification);
        kafkaTemplate.send(SHIPMENT_NOTIFICATION_TOPIC, shipmentNotification.getInstanceId(), shipmentNotification);
    }

    public void sendTransactionMark(TransactionMark transactionMark) {
        kafkaTemplate.send("TransactionMark_CUSTOMER_SESSION", transactionMark);
    }
}
