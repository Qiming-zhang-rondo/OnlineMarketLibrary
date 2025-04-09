package com.example.order.eventMessaging;

import com.example.common.events.*;
import com.example.order.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractOrderConsumer {

    private final IOrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(AbstractOrderConsumer.class);

    public AbstractOrderConsumer(IOrderService orderService) {
        this.orderService = orderService;
    }

    protected abstract StockConfirmed deserializeStockConfirmed(String payload);
    protected abstract PaymentConfirmed deserializePaymentConfirmed(String payload);
    protected abstract PaymentFailed deserializePaymentFailed(String payload);
    protected abstract ShipmentNotification deserializeShipmentNotification(String payload);

    public void handleStockConfirmed(String payload) {
        try {
            StockConfirmed stockConfirmed = deserializeStockConfirmed(payload);
            orderService.processStockConfirmed(stockConfirmed).join();
        } catch (Exception e) {
            logger.error("Failed to process StockConfirmed: {}", e.getMessage());
            try {
                StockConfirmed stockConfirmed = deserializeStockConfirmed(payload);
                CompletableFuture.runAsync(() -> orderService.processPoisonStockConfirmed(stockConfirmed));
            } catch (Exception inner) {
                logger.error("Failed to deserialize poison StockConfirmed: {}", inner.getMessage());
            }
        }
    }

    public void handlePaymentConfirmed(String payload) {
        try {
            PaymentConfirmed paymentConfirmed = deserializePaymentConfirmed(payload);
            orderService.processPaymentConfirmed(paymentConfirmed);
        } catch (Exception e) {
            logger.error("Failed to process PaymentConfirmed: {}", e.getMessage());
        }
    }

    public void handlePaymentFailed(String payload) {
        try {
            PaymentFailed paymentFailed = deserializePaymentFailed(payload);
            orderService.processPaymentFailed(paymentFailed);
        } catch (Exception e) {
            logger.error("Failed to process PaymentFailed: {}", e.getMessage());
        }
    }

    public void handleShipmentNotification(String payload) {
        try {
            ShipmentNotification shipmentNotification = deserializeShipmentNotification(payload);
            orderService.processShipmentNotification(shipmentNotification);
        } catch (Exception e) {
            logger.error("Failed to process ShipmentNotification: {}", e.getMessage());
        }
    }
}
