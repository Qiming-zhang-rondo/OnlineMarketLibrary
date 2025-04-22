package com.example.stock.kafka;

import com.example.common.events.ProductUpdated;
import com.example.common.events.ReserveStock;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.stock.eventMessaging.AbstractStockConsumer;
import com.example.stock.service.IStockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonStockConsumer extends AbstractStockConsumer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonStockConsumer(IStockService stockService) {
        super(stockService);
    }

    @Override
    protected ProductUpdated deserializeProductUpdated(String payload) {
        try {
            return objectMapper.readValue(payload, ProductUpdated.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for ProductUpdated", e);
        }
    }

    @Override
    protected ReserveStock deserializeReserveStock(String payload) {
        try {
            return objectMapper.readValue(payload, ReserveStock.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for ReserveStock", e);
        }
    }

    @Override
    protected PaymentConfirmed deserializePaymentConfirmed(String payload) {
        try {
            return objectMapper.readValue(payload, PaymentConfirmed.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for PaymentConfirmed", e);
        }
    }

    @Override
    protected PaymentFailed deserializePaymentFailed(String payload) {
        try {
            return objectMapper.readValue(payload, PaymentFailed.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for PaymentFailed", e);
        }
    }
}

