package com.example.customer.eventMessaging;

import com.example.common.events.DeliveryNotification;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.customer.service.ICustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCustomerConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCustomerConsumer.class);

    protected final ICustomerService customerService;

    protected AbstractCustomerConsumer(ICustomerService customerService) {
        this.customerService = customerService;
    }

    protected abstract DeliveryNotification deserializeDeliveryNotification(String payload);
    protected abstract PaymentConfirmed deserializePaymentConfirmed(String payload);
    protected abstract PaymentFailed deserializePaymentFailed(String payload);

    public void handleDeliveryNotification(String payload) {
        try {
            DeliveryNotification event = deserializeDeliveryNotification(payload);
            customerService.processDeliveryNotification(event);
        } catch (Exception e) {
            logger.error("Failed to process delivery notification: {}", e.getMessage());
        }
    }

    public void handlePaymentConfirmed(String payload) {
        try {
            PaymentConfirmed event = deserializePaymentConfirmed(payload);
            customerService.processPaymentConfirmed(event);
        } catch (Exception e) {
            logger.error("Failed to process payment confirmation: {}", e.getMessage());
        }
    }

    public void handlePaymentFailed(String payload) {
        try {
            PaymentFailed event = deserializePaymentFailed(payload);
            customerService.processPaymentFailed(event);
        } catch (Exception e) {
            logger.error("Failed to process payment failure: {}", e.getMessage());
        }
    }
}