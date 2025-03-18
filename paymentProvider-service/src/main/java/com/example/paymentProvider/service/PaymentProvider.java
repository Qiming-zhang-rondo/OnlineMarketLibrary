package com.example.paymentProvider.service;

import com.example.common.integration.PaymentIntent;
import com.example.common.integration.PaymentIntentCreateOptions;
import com.example.paymentProvider.infra.PaymentProviderConfig;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

@Service
public class PaymentProvider {

    @Autowired
    private final PaymentProviderConfig config;

    private final Map<String, PaymentIntent> db = new ConcurrentHashMap<>();

    @Autowired
    public PaymentProvider(PaymentProviderConfig config) {
        this.config = config;
    }

    public PaymentIntent processPaymentIntent(PaymentIntentCreateOptions options) {
        if (db.containsKey(options.getIdempotencyKey())) {
            return db.get(options.getIdempotencyKey());
        }

        Random random = new Random();
        String status = "succeeded";
        if (random.nextInt(100) < config.getFailPercentage()) {
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
