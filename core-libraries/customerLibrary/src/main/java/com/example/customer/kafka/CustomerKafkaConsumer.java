package com.example.customer.kafka;

import com.example.common.events.DeliveryNotification;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.customer.service.CustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CustomerKafkaConsumer {
    private final Logger logger = LoggerFactory.getLogger(CustomerKafkaConsumer.class);

    private final CustomerService customerService;

    public CustomerKafkaConsumer(CustomerService customerService) {
        this.customerService = customerService;
    }

    @KafkaListener(topics = "delivery-notification-topic", groupId = "customer-group")
    public void listenDeliveryNotification(DeliveryNotification deliveryNotification) {
        customerService.processDeliveryNotification(deliveryNotification);
    }

    @KafkaListener(topics = "payment-confirmed-topic", groupId = "customer-group")
    public void listenPaymentConfirmed(PaymentConfirmed paymentConfirmed) {

        customerService.processPaymentConfirmed(paymentConfirmed);
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "customer-group")
    public void listenPaymentFailed(PaymentFailed paymentFailed) {
        customerService.processPaymentFailed(paymentFailed);
    }
}
