package com.example.paymentProvider.controller;

import com.example.common.integration.PaymentIntent;
import com.example.common.integration.PaymentIntentCreateOptions;
import com.example.paymentProvider.service.IPaymentProvider;
import com.example.paymentProvider.config.PaymentProviderConfig;
import com.example.paymentProvider.service.PaymentProviderCore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentProviderController {

    private final IPaymentProvider paymentProvider;

    private static final Logger logger = LoggerFactory.getLogger(PaymentProviderController.class);

    @Autowired
    public PaymentProviderController(IPaymentProvider paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    @PostMapping("/esp")
    public ResponseEntity<PaymentIntent> processPaymentIntent(@RequestBody PaymentIntentCreateOptions options) {
        PaymentIntent paymentIntent = paymentProvider.processPaymentIntent(options);
        return new ResponseEntity<>(paymentIntent, HttpStatus.OK);
    }
}