package com.example.paymentProvider.service;

import com.example.common.integration.PaymentIntent;
import com.example.common.integration.PaymentIntentCreateOptions;
import com.example.paymentProvider.config.IPaymentProviderConfig;

import java.util.Map;
import java.util.UUID;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentProviderCore implements IPaymentProvider {

    private final IPaymentProviderConfig config;
    private final Map<String, PaymentIntent> db = new ConcurrentHashMap<>();

    public PaymentProviderCore(IPaymentProviderConfig config) {
        this.config = config;
    }

    @Override
    public PaymentIntent processPaymentIntent(PaymentIntentCreateOptions options) {
        if (db.containsKey(options.getIdempotencyKey())) {
            return db.get(options.getIdempotencyKey());
        }

        Random random = new Random();
        String status = (random.nextInt(100) < config.getFailPercentage()) ? "canceled" : "succeeded";

        PaymentIntent intent = new PaymentIntent();
        intent.setId(UUID.randomUUID().toString());
        intent.setAmount(options.getAmount());
        intent.setCustomer(options.getCustomer());
        intent.setStatus(status);
        intent.setCurrency(options.getCurrency().toString());
        intent.setCreated((int) System.currentTimeMillis());

        db.putIfAbsent(options.getIdempotencyKey(), intent);
        return intent;
    }
}