package com.example.order.kafka;
import com.example.common.events.*;
import com.example.order.eventMessaging.AbstractOrderConsumer;
import com.example.order.service.IOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonOrderConsumer extends AbstractOrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JsonOrderConsumer.class);
    @Autowired
    private ObjectMapper objectMapper;

    public JsonOrderConsumer(IOrderService orderService) {
        super(orderService);

    }

    @Override
    protected StockConfirmed deserializeStockConfirmed(String payload) {
        try {
            return objectMapper.readValue(payload, StockConfirmed.class);
        } catch (Exception e) {
            logger.error("Failed to deserialize StockConfirmed: {}", e.getMessage());
            return null;
        }
    }

    @Override
    protected PaymentConfirmed deserializePaymentConfirmed(String payload) {
        try {
            return objectMapper.readValue(payload, PaymentConfirmed.class);
        } catch (Exception e) {
            logger.error("Failed to deserialize PaymentConfirmed: {}", e.getMessage());
            return null;
        }
    }

    @Override
    protected PaymentFailed deserializePaymentFailed(String payload) {
        try {
            return objectMapper.readValue(payload, PaymentFailed.class);
        } catch (Exception e) {
            logger.error("Failed to deserialize PaymentFailed: {}", e.getMessage());
            return null;
        }
    }

    @Override
    protected ShipmentNotification deserializeShipmentNotification(String payload) {
        try {
            return objectMapper.readValue(payload, ShipmentNotification.class);
        } catch (Exception e) {
            logger.error("Failed to deserialize ShipmentNotification: {}", e.getMessage());
            return null;
        }
    }
}
