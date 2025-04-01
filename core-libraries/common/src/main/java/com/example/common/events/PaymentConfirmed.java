package com.example.common.events;

import java.time.LocalDateTime;
import java.util.List;
import com.example.common.requests.CustomerCheckout;
import com.example.common.entities.OrderItem;

public final class PaymentConfirmed {

    private CustomerCheckout customer;
    private int orderId;
    private float totalAmount;
    private List<OrderItem> items;
    private LocalDateTime date;
    private String instanceId;

    public PaymentConfirmed(){}

    // Constructor with parameters
    public PaymentConfirmed(CustomerCheckout customer, int orderId, float totalAmount, List<OrderItem> items, LocalDateTime date, String instanceId) {
        this.customer = customer;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.items = items;
        this.date = date;
        this.instanceId = instanceId;
    }

    // Getters and setters
    public CustomerCheckout getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerCheckout customer) {
        this.customer = customer;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
