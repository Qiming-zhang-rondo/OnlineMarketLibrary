package com.example.shipment.service;

import com.example.common.events.PaymentConfirmed;

public interface IShipmentService {

    
    void processShipment(PaymentConfirmed paymentConfirmed);

   // Update the Shipment information, passing in an instance ID
    void updateShipment(String instanceId);

    
    void cleanup();

    // Handling incorrect Shipment requests is similar to Poison event handling
    void processPoisonShipment(PaymentConfirmed paymentRequest);
}
