package com.example.payment.service;

import com.example.common.integration.PaymentIntent;
import com.example.common.integration.PaymentIntentCreateOptions;

public interface IExternalProvider {
    PaymentIntent create(PaymentIntentCreateOptions options);
}
