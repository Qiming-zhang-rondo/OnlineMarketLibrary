package com.example.shipment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ShipmentId implements Serializable {
    @Column(name = "customer_id", nullable = false)
    private int customerId;
    @Column(name = "order_id", nullable = false)
    private int orderId;

    
    public ShipmentId() {
    }


    public ShipmentId(int customerId, int orderId) {
        this.customerId = customerId;
        this.orderId = orderId;
    }

    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipmentId that = (ShipmentId) o;
        return customerId == that.customerId && orderId == that.orderId;
    }


    @Override
    public int hashCode() {
        return Objects.hash(customerId, orderId);
    }
}
