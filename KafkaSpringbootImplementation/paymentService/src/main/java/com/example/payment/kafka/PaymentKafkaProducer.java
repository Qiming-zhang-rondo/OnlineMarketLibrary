package com.example.payment.kafka;

import com.example.common.driver.TransactionMark;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.common.messaging.IEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentKafkaProducer implements IEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PaymentKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PAYMENT_CONFIRMED_TOPIC = "payment-confirmed-topic";
    private static final String PAYMENT_FAILED_TOPIC = "payment-failed-topic";
    private static final String TRANSACTION_TOPIC = "TransactionMark_CUSTOMER_SESSION";

    @Override
    public void publishEvent(String topic, Object event) {
        sendAsJson(topic, event);
    }

    public void sendPaymentConfirmedEvent(PaymentConfirmed event) {
        sendAsJson(PAYMENT_CONFIRMED_TOPIC, event);
        logger.info("Sent PaymentConfirmed event: {}", event);
    }

    public void sendPaymentFailedEvent(PaymentFailed event) {
        sendAsJson(PAYMENT_FAILED_TOPIC, event);
        logger.info("Sent PaymentFailed event: {}", event);
    }

    public void sendPoisonPaymentEvent(TransactionMark event) {
        sendAsJson(TRANSACTION_TOPIC, event);
        logger.info("Sent Poison Payment (TransactionMark) event: {}", event);
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