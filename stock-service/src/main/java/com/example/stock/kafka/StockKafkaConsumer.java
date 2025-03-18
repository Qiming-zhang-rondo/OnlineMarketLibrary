package com.example.stock.kafka;

import com.example.common.events.ProductUpdated;
import com.example.common.events.ReserveStock;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.stock.service.StockService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StockKafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(StockKafkaConsumer.class);

    @Autowired
    private StockService stockService;

    @KafkaListener(topics = "product-update-topic", groupId = "stock-group")
    public void listenProductUpdate(ProductUpdated productUpdated) {
        try {
            logger.info("receive product-update-topic");
            stockService.processProductUpdate(productUpdated);
        } catch (Exception e) {
            stockService.processPoisonProductUpdate(productUpdated);
        }
    }

    @KafkaListener(topics = "reserve-stock-topic", groupId = "stock-group")
    public void listenReserveStock(ReserveStock reserveStock) {
        try {
            logger.info("Already receive reserve-stock-topic");
            stockService.reserveStock(reserveStock);
            logger.info("Success deal with reserve-stock-topic");
        } catch (Exception e) {
            logger.warn("failed deal with reserve-stock-topic");
            stockService.processPoisonReserveStock(reserveStock);
        }
    }

    @KafkaListener(topics = "payment-confirmed-topic", groupId = "stock-group")
    public void listenPaymentConfirmed(PaymentConfirmed paymentConfirmed) {
        try {
            stockService.confirmReservation(paymentConfirmed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "stock-group")
    public void listenPaymentFailed(PaymentFailed paymentFailed) {
        try {
            stockService.cancelReservation(paymentFailed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
