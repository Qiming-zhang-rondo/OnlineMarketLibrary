package com.example.cart.kafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.common.driver.TransactionMark;
import com.example.common.driver.TransactionType;
import com.example.common.events.ProductUpdated;
import com.example.common.events.ReserveStock;
import com.example.cart.model.ReserveInventory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CartKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(CartKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, ProductUpdated> productKafkaTemplate;
    @Autowired
    private KafkaTemplate<String, ReserveStock> reservKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, TransactionMark> transactionMarkKafkaTemplate;


    private static final String PRODUCT_REQUEST_TOPIC = "product-request-topic";
    private static final String RESERVE_STOCK_TOPIC = "reserve-stock-topic";


    public void sendProductRequest(ProductUpdated productRequest) {
        productKafkaTemplate.send(PRODUCT_REQUEST_TOPIC, productRequest);  
    }
    public void sendReserveStock(ReserveStock reserveStock){
        reservKafkaTemplate.send(RESERVE_STOCK_TOPIC,reserveStock);
        logger.info("Already sent reserve-stock-topic");
    }
    public void sendPoisonCheckout(TransactionMark transactionMark) {
        transactionMarkKafkaTemplate.send("TransactionMark_CUSTOMER_SESSION", transactionMark);
    }
    public void sendPoisonPriceUpdate(TransactionMark transactionMark) {
        transactionMarkKafkaTemplate.send("TransactionMark_PRICE_UPDATE", transactionMark);
    }

    public void sendPoisonProductUpdated(TransactionMark transactionMark) {
        transactionMarkKafkaTemplate.send("TransactionMark_UPDATE_PRODUCT", transactionMark);
    }

    public void sendPriceUpdateTransactionMark(TransactionMark transactionMark) {
        transactionMarkKafkaTemplate.send("TransactionMark_PRICE_UPDATE", transactionMark);
    }
}
