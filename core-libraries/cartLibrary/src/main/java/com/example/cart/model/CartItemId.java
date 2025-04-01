package com.example.cart.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
@Embeddable
public class CartItemId implements Serializable {

    private int customerId;
    private int sellerId;
    private int productId;

    public CartItemId() {}

    public CartItemId(int customerId, int sellerId, int productId) {
        this.customerId = customerId;
        this.sellerId = sellerId;
        this.productId = productId;
    }

    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemId that = (CartItemId) o;
        return customerId == that.customerId && sellerId == that.sellerId && productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, sellerId, productId);
    }
}
