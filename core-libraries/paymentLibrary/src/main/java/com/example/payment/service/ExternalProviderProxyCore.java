package com.example.payment.service;

import com.example.common.integration.PaymentIntent;
import com.example.common.integration.PaymentIntentCreateOptions;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Random;

public class ExternalProviderProxyCore implements IExternalProvider {

    private final int failPercentage;
    private final Map<String, PaymentIntent> db = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public ExternalProviderProxyCore(int failPercentage) {
        this.failPercentage = failPercentage;
    }

    @Override
    public PaymentIntent create(PaymentIntentCreateOptions options) {
        if (db.containsKey(options.getIdempotencyKey())) {
            return db.get(options.getIdempotencyKey());
        }

        String status = "succeeded";
        if (random.nextInt(100) < failPercentage) {
            status = "canceled";
        }

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
