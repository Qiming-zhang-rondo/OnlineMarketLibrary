package com.example.payment.kafka;

import com.example.common.events.InvoiceIssued;
import com.example.payment.eventMessaging.AbstractPaymentConsumer;
import com.example.payment.service.IPaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonPaymentConsumer extends AbstractPaymentConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JsonPaymentConsumer.class);
    private final ObjectMapper objectMapper;

    public JsonPaymentConsumer(IPaymentService paymentService, ObjectMapper objectMapper) {
        super(paymentService);
        this.objectMapper = objectMapper;
    }

    @Override
    protected InvoiceIssued deserializeInvoiceIssued(String payload) {
        try {
            return objectMapper.readValue(payload, InvoiceIssued.class);
        } catch (Exception e) {
            logger.error("Error deserializing InvoiceIssued from payload: {}", payload, e);
            throw new RuntimeException("Deserialization failed", e);
        }
    }
}