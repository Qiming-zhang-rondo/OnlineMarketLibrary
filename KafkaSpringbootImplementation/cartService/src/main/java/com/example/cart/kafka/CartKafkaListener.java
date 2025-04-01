package com.example.cart.kafka;

import com.example.cart.kafka.JsonCartConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CartKafkaListener {

    private final JsonCartConsumer jsonCartConsumer;

    public CartKafkaListener(JsonCartConsumer jsonCartConsumer) {
        this.jsonCartConsumer = jsonCartConsumer;
    }

    @KafkaListener(topics = "price-update-topic", groupId = "cart-group")
    public void listenPriceUpdate(String message) {
        jsonCartConsumer.handlePriceUpdate(message);
    }

    @KafkaListener(topics = "product-update-topic", groupId = "cart-group")
    public void listenProductUpdate(String message) {
        jsonCartConsumer.handleProductUpdate(message);
    }
}