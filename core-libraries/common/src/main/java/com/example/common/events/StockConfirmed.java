package com.example.common.events;

import com.example.common.requests.CustomerCheckout;
import com.example.common.entities.CartItem;

import java.time.LocalDateTime;
import java.util.List;

public class StockConfirmed {

    private LocalDateTime timestamp;
    private CustomerCheckout customerCheckout;
    private List<CartItem> items;
    private String instanceId;


    public StockConfirmed() {}

  
    public StockConfirmed(LocalDateTime timestamp, CustomerCheckout customerCheckout, List<CartItem> items, String instanceId) {
        this.timestamp = timestamp;
        this.customerCheckout = customerCheckout;
        this.items = items;
        this.instanceId = instanceId;
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public CustomerCheckout getCustomerCheckout() {
        return customerCheckout;
    }

    public void setCustomerCheckout(CustomerCheckout customerCheckout) {
        this.customerCheckout = customerCheckout;
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
}
