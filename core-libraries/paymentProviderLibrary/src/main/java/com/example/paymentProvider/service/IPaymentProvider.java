package com.example.paymentProvider.service;

import com.example.common.integration.PaymentIntent;
import com.example.common.integration.PaymentIntentCreateOptions;

public interface IPaymentProvider {
    PaymentIntent processPaymentIntent(PaymentIntentCreateOptions options);
}
