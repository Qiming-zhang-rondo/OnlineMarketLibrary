package com.example.seller.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable  
public class OrderEntryId implements Serializable {

    private int customerId;
    private int orderId;
    private int sellerId;
    private int productId;

 
    public OrderEntryId() {}

 
    public OrderEntryId(int customerId, int orderId, int sellerId, int productId) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.sellerId = sellerId;
        this.productId = productId;
    }

    // Getter and Setter 
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

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    // override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntryId that = (OrderEntryId) o;
        return customerId == that.customerId &&
                orderId == that.orderId &&
                sellerId == that.sellerId &&
                productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, orderId, sellerId, productId);
    }
}
