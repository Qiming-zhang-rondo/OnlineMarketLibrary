package com.example.common.entities;

import java.util.ArrayList;
import java.util.List;

public class Cart {

   
    private int customerId = 0;

    private CartStatus status = CartStatus.OPEN;

    private List<CartItem> items = new ArrayList<>();

    private String instanceId;

    // To return
    private List<ProductStatus> divergencies;

    // Default constructor for serialization/deserialization
    public Cart() {}

    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public List<ProductStatus> getDivergencies() {
        return divergencies;
    }

    public void setDivergencies(List<ProductStatus> divergencies) {
        this.divergencies = divergencies;
    }

    // Override toString
    @Override
    public String toString() {
        return new StringBuilder()
                .append("customerId: ").append(customerId)
                .append(", status: ").append(status.toString())
                .toString();
    }
}
