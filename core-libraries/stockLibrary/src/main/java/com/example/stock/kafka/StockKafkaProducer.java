package com.example.stock.kafka;

import com.example.common.events.*;
import com.example.stock.model.StockItem;
import com.example.common.driver.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(StockKafkaProducer.class);
    private final KafkaTemplate<String, TransactionMark> transactionMarkKafkaTemplate;
    private final KafkaTemplate<String, StockConfirmed> stockConfirmedKafkaTemplate;
    private final KafkaTemplate<String, ReserveStockFailed> reserveStockFailedKafkaTemplate;
    private final KafkaTemplate<String, StockItem> stockItemKafkaTemplate;

    public StockKafkaProducer(
        KafkaTemplate<String, TransactionMark> transactionMarkKafkaTemplate,
        KafkaTemplate<String, StockConfirmed> stockConfirmedKafkaTemplate,
        KafkaTemplate<String, ReserveStockFailed> reserveStockFailedKafkaTemplate,
        KafkaTemplate<String, StockItem> stockItemKafkaTemplate
    ) {
        this.transactionMarkKafkaTemplate = transactionMarkKafkaTemplate;
        this.stockConfirmedKafkaTemplate = stockConfirmedKafkaTemplate;
        this.reserveStockFailedKafkaTemplate = reserveStockFailedKafkaTemplate;
        this.stockItemKafkaTemplate = stockItemKafkaTemplate;
    }

    public void sendProductUpdate(TransactionMark transactionMark) {
        transactionMarkKafkaTemplate.send("TransactionMark_UPDATE_PRODUCT", transactionMark);
        logger.info("already send TransactionMark_UPDATE_PRODUCT : {}",transactionMark);
    }

    public void sendStockConfirmed(StockConfirmed stockConfirmed) {
        stockConfirmedKafkaTemplate.send("stock-confirmed-topic", stockConfirmed);
        logger.info("Already send stock-confirmed-topic");
    }

    public void sendStockFailed(ReserveStockFailed reserveStockFailed) {
        reserveStockFailedKafkaTemplate.send("stock-failed-topic", reserveStockFailed);
    }

    public void sendPoisonProductUpdate(TransactionMark transactionMark) {
        transactionMarkKafkaTemplate.send("TransactionMark_UPDATE_PRODUCT", transactionMark);
    }

    public void sendPoisonReserveStock(TransactionMark transactionMark) {
        transactionMarkKafkaTemplate.send("TransactionMark_CUSTOMER_SESSION", transactionMark);
    }

    public void sendReserveStockFailed(ReserveStockFailed reserveFailed) {
        reserveStockFailedKafkaTemplate.send("stock-reserve-failed-topic", reserveFailed);
    }

    public void sendTransactionMark(TransactionMark transactionMark) {
        transactionMarkKafkaTemplate.send("TransactionMark_CUSTOMER_SESSION", transactionMark);
    }

    public void sendStockUpdate(StockItem stockItem) {
        stockItemKafkaTemplate.send("stock-update-topic", stockItem);
    }
}
