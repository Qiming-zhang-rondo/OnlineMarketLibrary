package com.example.customer.service;

import com.example.common.events.DeliveryNotification;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;

public interface ICustomerService {

    void processDeliveryNotification(DeliveryNotification paymentConfirmed);

    void processPaymentConfirmed(PaymentConfirmed paymentConfirmed);

    void processPaymentFailed(PaymentFailed paymentFailed);

    void cleanup();

    void reset();
}

