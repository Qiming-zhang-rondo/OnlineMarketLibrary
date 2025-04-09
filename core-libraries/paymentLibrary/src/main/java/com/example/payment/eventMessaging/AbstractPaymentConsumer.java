package com.example.payment.eventMessaging;

import com.example.common.events.InvoiceIssued;
import com.example.payment.service.IPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractPaymentConsumer {

    protected final IPaymentService paymentService;
    private final Logger logger = LoggerFactory.getLogger(AbstractPaymentConsumer.class);

    public AbstractPaymentConsumer(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    protected abstract InvoiceIssued deserializeInvoiceIssued(String payload);

    public void handleInvoiceIssued(String payload) {
        try {
            InvoiceIssued invoiceIssued = deserializeInvoiceIssued(payload);
            paymentService.processPayment(invoiceIssued);
        } catch (Exception e) {
            logger.error("Failed to process InvoiceIssued: {}", e.getMessage());
            try {
                InvoiceIssued invoiceIssued = deserializeInvoiceIssued(payload);
                CompletableFuture.runAsync(() -> paymentService.processPoisonPayment(invoiceIssued));
            } catch (Exception inner) {
                logger.error("Failed to deserialize poison InvoiceIssued: {}", inner.getMessage());
            }
        }
    }
}