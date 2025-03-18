package com.example.order.kafka;

import com.example.common.events.*;
import com.example.order.service.OrderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderKafkaConsumer {

    private final OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(OrderKafkaConsumer.class);

    public OrderKafkaConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "stock-confirmed-topic", groupId = "order-group")
    public void processStockConfirmed(ConsumerRecord<String, StockConfirmed> record) {
        StockConfirmed stockConfirmed = record.value();
        try {
            
            orderService.processStockConfirmed(stockConfirmed).join();
        } catch (Exception e) {
            logger.error("Error processing StockConfirmed event: {}", e.getMessage());
            CompletableFuture.runAsync(() -> orderService.processPoisonStockConfirmed(stockConfirmed));
        }
    }

    @KafkaListener(topics = "payment-confirmed-topic", groupId = "order-group")
    public void processPaymentConfirmed(ConsumerRecord<String, PaymentConfirmed> record) {
        PaymentConfirmed paymentConfirmed = record.value();
        try {
            orderService.processPaymentConfirmed(paymentConfirmed);
        } catch (Exception e) {
            logger.error("Error processing PaymentConfirmed event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "order-group")
    public void processPaymentFailed(ConsumerRecord<String, PaymentFailed> record) {
        logger.info("receive topic");
        PaymentFailed paymentFailed = record.value();
        logger.info("PaymentFailed record value: {}", paymentFailed);
        try {
            logger.info("enter try");
            orderService.processPaymentFailed(paymentFailed);
        } catch (Exception e) {
            logger.error("Error processing PaymentFailed event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "shipment-notification-topic", groupId = "order-group")
    public void processShipmentNotification(ConsumerRecord<String, ShipmentNotification> record) {
        ShipmentNotification shipmentNotification = record.value();
        try {
            orderService.processShipmentNotification(shipmentNotification);
        } catch (Exception e) {
            logger.error("Error processing ShipmentNotification event: {}", e.getMessage());
        }
    }
}
