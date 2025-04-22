package com.example.seller.eventMessaging;

import com.example.common.events.DeliveryNotification;
import com.example.common.events.InvoiceIssued;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.common.events.ShipmentNotification;
import com.example.seller.service.ISellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象消费者类，只定义处理各种事件的通用方法。
 * 不包含任何 Kafka 相关注解，也不依赖 Spring。
 */
public abstract class AbstractSellerConsumer {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final ISellerService sellerService;

    protected AbstractSellerConsumer(ISellerService sellerService) {
        this.sellerService = sellerService;
    }

    protected abstract InvoiceIssued deserializeInvoiceIssued(String payload);

    protected abstract PaymentFailed deserializePaymentFailed(String payload);

    protected abstract ShipmentNotification deserializeShipmentNotification(String payload);

    protected abstract DeliveryNotification deserializeDeliveryNotification(String payload);

    protected abstract PaymentConfirmed deserializePaymentConfirmed(String payload);

    public void handleInvoiceIssued(String payload) {
        try {
            InvoiceIssued invoiceIssued = deserializeInvoiceIssued(payload);
            sellerService.processInvoiceIssued(invoiceIssued);
            logger.info("Processed InvoiceIssued event for orderId: {}", invoiceIssued.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing InvoiceIssued event: {}", e.getMessage());
        }
    }

    public void handlePaymentFailed(String payload) {
        try {
            PaymentFailed paymentFailed = deserializePaymentFailed(payload);
            sellerService.processPaymentFailed(paymentFailed);
            logger.info("Processed PaymentFailed event for orderId: {}", paymentFailed.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing PaymentFailed event: {}", e.getMessage());
        }
    }

    public void handleShipmentNotification(String payload) {
        try {
            ShipmentNotification shipmentNotification = deserializeShipmentNotification(payload);
            sellerService.processShipmentNotification(shipmentNotification);
            logger.info("Processed ShipmentNotification event for orderId: {}", shipmentNotification.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing ShipmentNotification event: {}", e.getMessage());
        }
    }

    public void handleDeliveryNotification(String payload) {
        try {
            DeliveryNotification deliveryNotification = deserializeDeliveryNotification(payload);
            sellerService.processDeliveryNotification(deliveryNotification);
            logger.info("Processed DeliveryNotification event for orderId: {}", deliveryNotification.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing DeliveryNotification event: {}", e.getMessage());
        }
    }

    public void handlePaymentConfirmed(String payload) {
        try {
            PaymentConfirmed paymentConfirmed = deserializePaymentConfirmed(payload);
            sellerService.processPaymentConfirmed(paymentConfirmed);
            logger.info("Processed PaymentConfirmed event for orderId: {}", paymentConfirmed.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing PaymentConfirmed event: {}", e.getMessage());
        }
    }


}

