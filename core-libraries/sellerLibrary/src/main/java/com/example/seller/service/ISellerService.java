package com.example.seller.service;

import com.example.common.events.*;
import com.example.seller.dto.SellerDashboard;

public interface ISellerService {

    void processInvoiceIssued(InvoiceIssued invoiceIssued);

    void processPaymentConfirmed(PaymentConfirmed paymentConfirmed);

    void processPaymentFailed(PaymentFailed paymentFailed);

    void processShipmentNotification(ShipmentNotification shipmentNotification);

    void processDeliveryNotification(DeliveryNotification deliveryNotification);

    SellerDashboard queryDashboard(int sellerId);

    void cleanup();

    void reset();
}
