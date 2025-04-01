package com.example.cart.kafka;

import com.example.common.driver.TransactionMark;
import com.example.common.events.ProductUpdated;
import com.example.common.events.ReserveStock;
import com.example.common.messaging.IEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartKafkaProducer implements IEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(CartKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PRODUCT_REQUEST_TOPIC = "product-request-topic";
    private static final String RESERVE_STOCK_TOPIC = "reserve-stock-topic";
    private static final String TRANSACTION_MARK_CUSTOMER_SESSION = "TransactionMark_CUSTOMER_SESSION";
    private static final String TRANSACTION_MARK_PRICE_UPDATE = "TransactionMark_PRICE_UPDATE";
    private static final String TRANSACTION_MARK_UPDATE_PRODUCT = "TransactionMark_UPDATE_PRODUCT";


    @Override
    public void publishEvent(String topic, Object event) {
        sendAsJson(topic, event);
    }

  
    public void sendProductRequest(ProductUpdated productRequest) {
        sendAsJson(PRODUCT_REQUEST_TOPIC, productRequest);
    }

    public void sendReserveStock(ReserveStock reserveStock){
        sendAsJson(RESERVE_STOCK_TOPIC, reserveStock);
        logger.info("Sent to reserve-stock-topic");
    }

    public void sendPoisonCheckout(TransactionMark transactionMark) {
        sendAsJson(TRANSACTION_MARK_CUSTOMER_SESSION, transactionMark);
    }

    public void sendPoisonPriceUpdate(TransactionMark transactionMark) {
        sendAsJson(TRANSACTION_MARK_PRICE_UPDATE, transactionMark);
    }

    public void sendPoisonProductUpdated(TransactionMark transactionMark) {
        sendAsJson(TRANSACTION_MARK_UPDATE_PRODUCT, transactionMark);
    }

    public void sendPriceUpdateTransactionMark(TransactionMark transactionMark) {
        sendAsJson(TRANSACTION_MARK_PRICE_UPDATE, transactionMark);
    }


    private void sendAsJson(String topic, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(new ProducerRecord<>(topic, json));
        } catch (Exception e) {
            logger.error("Failed to serialize and send message to topic {}: {}", topic, e.getMessage());
        }
    }
}