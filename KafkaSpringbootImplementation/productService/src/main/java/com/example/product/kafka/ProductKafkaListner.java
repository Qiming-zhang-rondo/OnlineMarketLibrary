package com.example.product.kafka;

import com.example.common.events.ProductUpdated;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

public class ProductKafkaListner {
    private final JsonProductConsumer jsonProductConsumer;


    public ProductKafkaListner(JsonProductConsumer jsonProductConsumer) {
        this.jsonProductConsumer = jsonProductConsumer;
    }

    @KafkaListener(topics = "product-request-topic", groupId = "product-group")
    public void consumeProductRequest(String message) {jsonProductConsumer.handleProductRequest(message);}
}
