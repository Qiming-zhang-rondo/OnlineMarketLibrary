package com.example.stock.kafka;

import com.example.common.driver.TransactionMark;
import com.example.common.events.DeliveryNotification;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.common.events.ReserveStock;
import com.example.common.events.StockConfirmed;
import com.example.common.events.ReserveStockFailed;
import com.example.common.messaging.IEventPublisher;
import com.example.stock.model.StockItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockKafkaProducer implements IEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(StockKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // 定义各个主题名称
    private static final String TRANSACTION_MARK_UPDATE_PRODUCT = "TransactionMark_UPDATE_PRODUCT";
    private static final String TRANSACTION_MARK_CUSTOMER_SESSION = "TransactionMark_CUSTOMER_SESSION";
    private static final String STOCK_CONFIRMED_TOPIC = "stock-confirmed-topic";
    private static final String STOCK_FAILED_TOPIC = "stock-failed-topic";
    private static final String STOCK_RESERVE_FAILED_TOPIC = "stock-reserve-failed-topic";
    private static final String STOCK_UPDATE_TOPIC = "stock-update-topic";

    @Override
    public void publishEvent(String topic, Object event) {
        sendAsJson(topic, event);
    }

    public void sendProductUpdate(TransactionMark transactionMark) {
        sendAsJson(TRANSACTION_MARK_UPDATE_PRODUCT, transactionMark);
        logger.info("Sent TransactionMark_UPDATE_PRODUCT: {}", transactionMark);
    }

    public void sendStockConfirmed(StockConfirmed stockConfirmed) {
        sendAsJson(STOCK_CONFIRMED_TOPIC, stockConfirmed);
        logger.info("Sent stock-confirmed-topic.");
    }

    public void sendStockFailed(ReserveStockFailed reserveStockFailed) {
        sendAsJson(STOCK_FAILED_TOPIC, reserveStockFailed);
        logger.info("Sent stock-failed-topic.");
    }

    public void sendPoisonProductUpdate(TransactionMark transactionMark) {
        sendAsJson(TRANSACTION_MARK_UPDATE_PRODUCT, transactionMark);
        logger.info("Sent poison product update, TransactionMark_UPDATE_PRODUCT: {}", transactionMark);
    }

    public void sendPoisonReserveStock(TransactionMark transactionMark) {
        sendAsJson(TRANSACTION_MARK_CUSTOMER_SESSION, transactionMark);
        logger.info("Sent poison reserve stock, TransactionMark_CUSTOMER_SESSION: {}", transactionMark);
    }

    public void sendReserveStockFailed(ReserveStockFailed reserveFailed) {
        sendAsJson(STOCK_RESERVE_FAILED_TOPIC, reserveFailed);
        logger.info("Sent reserve stock failed, stock-reserve-failed-topic.");
    }

    public void sendTransactionMark(TransactionMark transactionMark) {
        sendAsJson(TRANSACTION_MARK_CUSTOMER_SESSION, transactionMark);
        logger.info("Sent transaction mark, TransactionMark_CUSTOMER_SESSION: {}", transactionMark);
    }

    public void sendStockUpdate(StockItem stockItem) {
        sendAsJson(STOCK_UPDATE_TOPIC, stockItem);
        logger.info("Sent stock update event for sellerId: {}, productId: {}",
                stockItem.getSellerId(), stockItem.getProductId());
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

