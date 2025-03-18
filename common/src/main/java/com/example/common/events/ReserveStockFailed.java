package com.example.common.events;

import com.example.common.requests.CustomerCheckout;
import com.example.common.entities.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ReserveStockFailed {

    private LocalDateTime timestamp;
    private CustomerCheckout customerCheckout;
    private List<ProductStatus> products;
    private String instanceId;


    public ReserveStockFailed() {}


    public ReserveStockFailed(LocalDateTime timestamp, CustomerCheckout customerCheckout, List<ProductStatus> products, String instanceId) {
        this.timestamp = timestamp;
        this.customerCheckout = customerCheckout;
        this.products = products;
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

    public List<ProductStatus> getProducts() {
        return products;
    }

    public void setProducts(List<ProductStatus> products) {
        this.products = products;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
