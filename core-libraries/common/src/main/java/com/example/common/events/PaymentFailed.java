package com.example.common.events;

import java.util.List;
import com.example.common.requests.CustomerCheckout;
import com.example.common.entities.OrderItem;

public class PaymentFailed {

    private String status;
    private CustomerCheckout customer;
    private int orderId;
    private List<OrderItem> items;
    private float totalAmount;
    private String instanceId;

    // Default constructor
    public PaymentFailed() {}

    // Constructor with parameters
    public PaymentFailed(String status, CustomerCheckout customer, int orderId, List<OrderItem> items, float totalAmount, String instanceId) {
        this.status = status;
        this.customer = customer;
        this.orderId = orderId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.instanceId = instanceId;
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
