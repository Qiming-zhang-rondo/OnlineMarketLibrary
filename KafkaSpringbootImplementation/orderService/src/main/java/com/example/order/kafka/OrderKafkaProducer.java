package com.example.order.kafka;

import com.example.common.driver.TransactionMark;
import com.example.common.events.InvoiceIssued;
import com.example.common.messaging.IEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaProducer implements IEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(OrderKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String INVOICE_ISSUED_TOPIC = "invoice-issued-topic";
    private static final String TRANSACTION_TOPIC = "TransactionMark_CUSTOMER_SESSION";

    @Override
    public void publishEvent(String topic, Object event) {
        sendAsJson(topic, event);
    }

    public void sendInvoiceIssued(InvoiceIssued invoiceIssued) {
        sendAsJson(INVOICE_ISSUED_TOPIC, invoiceIssued);
        logger.info("Sent InvoiceIssued for orderId = {}", invoiceIssued.getOrderId());
    }

    public void sendTransactionMark(TransactionMark transactionMark) {
        sendAsJson(TRANSACTION_TOPIC, transactionMark);
        logger.info("Sent TransactionMark: {}", transactionMark);
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