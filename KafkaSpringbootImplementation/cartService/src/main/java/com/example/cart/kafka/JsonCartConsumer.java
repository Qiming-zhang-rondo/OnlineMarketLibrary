package com.example.cart.kafka;

import com.example.cart.eventMessaging.AbstractCartConsumer;
import com.example.cart.service.ICartService;
import com.example.common.events.PriceUpdate;
import com.example.common.events.ProductUpdated;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonCartConsumer extends AbstractCartConsumer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonCartConsumer(ICartService cartService) {
        super(cartService);
    }

    @Override
    protected PriceUpdate deserializePriceUpdate(String payload) {
        try {
            return objectMapper.readValue(payload, PriceUpdate.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for PriceUpdate", e);
        }
    }

    @Override
    protected ProductUpdated deserializeProductUpdated(String payload) {
        try {
            return objectMapper.readValue(payload, ProductUpdated.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for ProductUpdated", e);
        }
    }
}