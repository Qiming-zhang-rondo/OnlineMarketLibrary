package com.example.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderHistoryId implements Serializable {
    @Column(name = "customer_id")
    private int customerId;
    @Column(name = "order_id")
    private int orderId;

    public OrderHistoryId() {}

    public OrderHistoryId(int customerId, int orderId) {
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

    // hashCode and equals methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderHistoryId that = (OrderHistoryId) o;
        return customerId == that.customerId && orderId == that.orderId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, orderId);
    }
}
