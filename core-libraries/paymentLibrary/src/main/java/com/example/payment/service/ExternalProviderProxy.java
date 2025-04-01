package com.example.payment.service;

import com.example.common.integration.PaymentIntent;
import com.example.common.integration.PaymentIntentCreateOptions;
import com.example.payment.infra.PaymentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalProviderProxy implements IExternalProvider {

    private final RestTemplate restTemplate;
    private final PaymentConfig config;
    private final Logger logger = LoggerFactory.getLogger(ExternalProviderProxy.class);
    @Autowired
    public ExternalProviderProxy(RestTemplate restTemplate, PaymentConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    @Override
public PaymentIntent create(PaymentIntentCreateOptions options) {
    try {
        // Log the URL and request data
        String url = config.getPaymentProviderUrl()+"/api/payment/esp";
        logger.info("Preparing to send request to URL: {}", url);
        logger.info("PaymentIntentCreateOptions - Amount: {}, Customer: {}, Idempotency Key: {}, Card Options: {}",
                options.getAmount(), options.getCustomer(), options.getIdempotencyKey(), options.getCardOptions());

    

        // Send POST request using RestTemplate
        PaymentIntent response = restTemplate.postForObject(url, options, PaymentIntent.class);

        logger.info("receive response");
        // Check and log the response
        if (response != null) {
            logger.info("Payment intent created successfully with ID: {}", response.getId());
            logger.info("Payment Intent Status: {}, Amount: {}, Currency: {}",
                    response.getStatus(), response.getAmount(), response.getCurrency());
        } else {
            logger.warn("Payment intent creation failed, received null response.");
        }

        return response;
    } catch (Exception e) {
        // Log the exception with the complete stack trace
        logger.error("Exception occurred while creating payment intent", e.toString());
        logger.debug("Error details: PaymentIntentCreateOptions - {}", options);
        return null;
    }
}

    
}
