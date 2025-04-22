package com.example.stock.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StockKafkaListener {

    private final JsonStockConsumer jsonStockConsumer;

    public StockKafkaListener(JsonStockConsumer jsonStockConsumer) {
        this.jsonStockConsumer = jsonStockConsumer;
    }

    @KafkaListener(topics = "product-update-topic", groupId = "stock-group")
    public void listenProductUpdate(String message) {
        jsonStockConsumer.handleProductUpdate(message);
    }

    @KafkaListener(topics = "reserve-stock-topic", groupId = "stock-group")
    public void listenReserveStock(String message) {
        jsonStockConsumer.handleReserveStock(message);
    }

    @KafkaListener(topics = "payment-confirmed-topic", groupId = "stock-group")
    public void listenPaymentConfirmed(String message) {
        jsonStockConsumer.handlePaymentConfirmed(message);
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "stock-group")
    public void listenPaymentFailed(String message) {
        jsonStockConsumer.handlePaymentFailed(message);
    }
}
