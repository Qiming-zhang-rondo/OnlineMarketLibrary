package com.example.seller.kafka;

import com.example.common.events.*;
import com.example.seller.service.SellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SellerKafkaConsumer {

    private final Logger logger = LoggerFactory.getLogger(SellerKafkaConsumer.class);
    private final SellerService sellerService;

    public SellerKafkaConsumer(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @KafkaListener(topics = "invoice-issued-topic", groupId = "seller-group")
    public void consumeInvoiceIssued(InvoiceIssued invoiceIssued) {
        try {
            sellerService.processInvoiceIssued(invoiceIssued);
            logger.info("Processed InvoiceIssued event for orderId: {}", invoiceIssued.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing InvoiceIssued event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "seller-group")
    public void consumePaymentFailed(PaymentFailed paymentFailed) {
        try {
            sellerService.processPaymentFailed(paymentFailed);
            logger.info("Processed PaymentFailed event for orderId: {}", paymentFailed.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing PaymentFailed event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "shipment-notification-topic", groupId = "seller-group")
    public void consumeShipmentNotification(ShipmentNotification shipmentNotification) {
        try {
            sellerService.processShipmentNotification(shipmentNotification);
            logger.info("Processed ShipmentNotification event for orderId: {}", shipmentNotification.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing ShipmentNotification event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "delivery-notification-topic", groupId = "seller-group")
    public void consumeDeliveryNotification(DeliveryNotification deliveryNotification) {
        try {
            sellerService.processDeliveryNotification(deliveryNotification);
            logger.info("Processed DeliveryNotification event for orderId: {}", deliveryNotification.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing DeliveryNotification event: {}", e.getMessage());
        }
    }

    
    @KafkaListener(topics = "payment-confirmed-topic", groupId = "seller-group")
    public void consumePaymentConfirmed(PaymentConfirmed paymentConfirmed) {
        try {
            sellerService.processPaymentConfirmed(paymentConfirmed);
            logger.info("Processed PaymentConfirmed event for orderId: {}", paymentConfirmed.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing PaymentConfirmed event: {}", e.getMessage());
        }
    }
    
}
