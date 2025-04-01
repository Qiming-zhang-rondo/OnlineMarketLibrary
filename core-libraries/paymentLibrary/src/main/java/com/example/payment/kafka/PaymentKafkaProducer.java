package com.example.payment.kafka;

import com.example.common.driver.TransactionMark;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentKafkaProducer {
    @Autowired
    private KafkaTemplate<String, PaymentConfirmed> paymentConfirmedkafkaTemplate;
    @Autowired
    private KafkaTemplate<String, PaymentFailed> paymentFailedKafkaTemplate;
    @Autowired
    private KafkaTemplate<String, TransactionMark> transactionkafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(PaymentKafkaProducer.class);


    public void sendPaymentConfirmedEvent(PaymentConfirmed event) {
        logger.info("Sending PaymentConfirmed event to Kafka: {}", event);
        paymentConfirmedkafkaTemplate.send("payment-confirmed-topic", event);
    }

    public void sendPaymentFailedEvent(PaymentFailed event) {
        logger.info("Sending PaymentFailed event to Kafka: {}", event);
        paymentFailedKafkaTemplate.send("payment-failed-topic", event);
    }

    public void sendPoisonPaymentEvent(TransactionMark event) {
        logger.info("Sending Poison Payment event to Kafka: {}", event);
        transactionkafkaTemplate.send("TransactionMark_CUSTOMER_SESSION" ,event);
    }
}
