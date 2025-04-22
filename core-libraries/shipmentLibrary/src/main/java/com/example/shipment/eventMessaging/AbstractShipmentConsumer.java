package com.example.shipment.eventMessaging;

import com.example.common.events.InvoiceIssued;
import com.example.common.events.PaymentConfirmed;
import com.example.shipment.service.IShipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象消费者类，用于处理 Shipment 相关事件，不依赖 Kafka 注解。
 */
public abstract class AbstractShipmentConsumer {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final IShipmentService shipmentService;

    protected abstract PaymentConfirmed deserializeInvoiceIssued(String payload);

    public AbstractShipmentConsumer(IShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    /**
     * 处理 PaymentConfirmed 事件
     */
    public void handlePaymentConfirmed(String payload) {
        try {
            PaymentConfirmed paymentConfirmed = deserializeInvoiceIssued(payload);
            logger.info("Received PaymentConfirmed event: {}", paymentConfirmed);
            shipmentService.processShipment(paymentConfirmed);
        } catch (Exception e) {
            PaymentConfirmed paymentConfirmed = deserializeInvoiceIssued(payload);
            logger.error("Error processing PaymentConfirmed event: {}", e.getMessage());
            shipmentService.processPoisonShipment(paymentConfirmed);
        }
    }
}

