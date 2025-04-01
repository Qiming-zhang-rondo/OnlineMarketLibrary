package com.example.payment.service;

import com.example.common.events.InvoiceIssued;

public interface IPaymentService {
    
    void processPayment(InvoiceIssued paymentRequest);

    void cleanup();

    void processPoisonPayment(InvoiceIssued invoice);
}
