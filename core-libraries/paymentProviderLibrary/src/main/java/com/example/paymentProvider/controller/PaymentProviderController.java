package com.example.paymentProvider.controller;

import com.example.common.integration.PaymentIntent;
import com.example.paymentProvider.service.PaymentProvider;
import com.example.common.integration.PaymentIntentCreateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentProviderController {

    private final PaymentProvider paymentProvider;
    private static final Logger logger = LoggerFactory.getLogger(PaymentProviderController.class);

    @Autowired
    public PaymentProviderController(PaymentProvider paymentProviderService) {
        this.paymentProvider = paymentProviderService;
    }

    @PostMapping("/esp")
    public ResponseEntity<PaymentIntent> processPaymentIntent(@RequestBody PaymentIntentCreateOptions options) {
        PaymentIntent paymentIntent = paymentProvider.processPaymentIntent(options);
        return new ResponseEntity<>(paymentIntent, HttpStatus.OK);
    }
}
