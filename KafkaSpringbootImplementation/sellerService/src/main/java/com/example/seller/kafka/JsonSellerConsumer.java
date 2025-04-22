package com.example.seller.kafka;

import com.example.seller.eventMessaging.AbstractSellerConsumer;
import com.example.common.events.DeliveryNotification;
import com.example.common.events.InvoiceIssued;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.common.events.ShipmentNotification;
import com.example.seller.service.ISellerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonSellerConsumer extends AbstractSellerConsumer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonSellerConsumer(ISellerService sellerService) {
        super(sellerService);
    }

    @Override
    protected InvoiceIssued deserializeInvoiceIssued(String payload) {
        try {
            return objectMapper.readValue(payload, InvoiceIssued.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for InvoiceIssued", e);
        }
    }

    @Override
    protected PaymentFailed deserializePaymentFailed(String payload) {
        try {
            return objectMapper.readValue(payload, PaymentFailed.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for PaymentFailed", e);
        }
    }

    @Override
    protected ShipmentNotification deserializeShipmentNotification(String payload) {
        try {
            return objectMapper.readValue(payload, ShipmentNotification.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for ShipmentNotification", e);
        }
    }

    @Override
    protected DeliveryNotification deserializeDeliveryNotification(String payload) {
        try {
            return objectMapper.readValue(payload, DeliveryNotification.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for DeliveryNotification", e);
        }
    }

    @Override
    protected PaymentConfirmed deserializePaymentConfirmed(String payload) {
        try {
            return objectMapper.readValue(payload, PaymentConfirmed.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialization failed for PaymentConfirmed", e);
        }
    }
}
