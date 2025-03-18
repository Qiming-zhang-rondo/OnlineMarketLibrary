package com.example.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderItemId implements Serializable {
    @Column(name = "customer_id")
    private int customerId;
    @Column(name = "order_id")
    private int orderId;
    @Column(name = "orderItem_id")
    private int orderItemId;

    public OrderItemId() {}

    public OrderItemId(int customerId, int orderId, int orderItemId) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.orderItemId = orderItemId;
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

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    // hashCode and equals methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return customerId == that.customerId && orderId == that.orderId && orderItemId == that.orderItemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, orderId, orderItemId);
    }
}
