package com.example.payment.kafka;

import com.example.common.events.InvoiceIssued;
import com.example.payment.service.PaymentService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentKafkaConsumer {

    private final PaymentService paymentService;
    private final Logger logger = LoggerFactory.getLogger(PaymentKafkaConsumer.class);

    public PaymentKafkaConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Kafka consumer listener
    @KafkaListener(topics = "invoice-issued-topic", groupId = "payment-group")
    public void listen(ConsumerRecord<String, InvoiceIssued> record) {
        try {
            InvoiceIssued invoiceIssued = record.value();
            logger.info("Received InvoiceIssued event for processing: {}", invoiceIssued);

            // call PaymentService 
            paymentService.processPayment(invoiceIssued);
        } catch (Exception e) {
            logger.error("Error processing payment: ", e);
            paymentService.processPoisonPayment(record.value());
        }
    }
}
