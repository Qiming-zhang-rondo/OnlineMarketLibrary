package com.example.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderPaymentCardId implements Serializable {
    @Column(name = "customer_id")
    private int customerId;
    @Column(name = "order_id")
    private int orderId;
    @Column(name = "sequential")
    private int sequential;

    // Constructors, getters, setters, equals, and hashCode
    public OrderPaymentCardId() {
    }

    public OrderPaymentCardId(int customerId, int orderId, int sequential) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.sequential = sequential;
    }

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

    public int getSequential() {
        return sequential;
    }

    public void setSequential(int sequential) {
        this.sequential = sequential;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderPaymentCardId that = (OrderPaymentCardId) o;
        return customerId == that.customerId &&
               orderId == that.orderId &&
               sequential == that.sequential;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, orderId, sequential);
    }
}
