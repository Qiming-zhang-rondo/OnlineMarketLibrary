package com.example.product.kafka;

import com.example.common.events.PriceUpdate;
import com.example.common.messaging.IEventPublisher;
import com.example.product.eventMessaging.AbstractProductConsumer;
import com.example.product.service.IProductService;
import com.example.common.events.ProductUpdated;
import com.example.product.repository.IProductRepository;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonProductConsumer extends AbstractProductConsumer{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //check
    public JsonProductConsumer(IProductRepository productRepository, IEventPublisher eventPublisher) {
        super(productRepository, eventPublisher);
    }


    @Override
    protected ProductUpdated deserializeProductUpdated(String productRequest) {
        try {
            return objectMapper.readValue(productRequest, ProductUpdated.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for PriceUpdate", e);
        }
    }
}
