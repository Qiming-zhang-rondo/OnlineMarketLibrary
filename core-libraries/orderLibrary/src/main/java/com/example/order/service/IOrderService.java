package com.example.order.service;

import com.example.common.events.*;
import java.util.concurrent.CompletableFuture;

public interface IOrderService {

    // Handling shipment notification
    void processShipmentNotification(ShipmentNotification notification);

    // Handling stock confirmed events
    CompletableFuture<Void> processStockConfirmed(StockConfirmed checkout);

    // Handling payment confirmed events
    void processPaymentConfirmed(PaymentConfirmed paymentConfirmed);

    // Handling payment failure events
    void processPaymentFailed(PaymentFailed paymentFailed);

    // clean database
    void cleanup();

    // Handle inventory confirmation error events
    CompletableFuture<Void> processPoisonStockConfirmed(StockConfirmed stockConfirmed);
}
