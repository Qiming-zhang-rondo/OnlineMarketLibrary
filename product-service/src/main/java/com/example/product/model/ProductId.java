package com.example.product.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class ProductId implements Serializable {

    @Column(name = "seller_id")
    @JsonProperty("seller_id")
    private int sellerId;

    @Column(name = "product_id")
    @JsonProperty("product_id")
    private int productId;

    // Default constructor
    public ProductId() {}

    // Constructor with fields
    public ProductId(int sellerId, int productId) {
        this.sellerId = sellerId;
        this.productId = productId;
    }

    // Getters and Setters
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

    // hashCode and equals methods (Important for composite keys)
    @Override
    public int hashCode() {
        return Objects.hash(sellerId, productId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProductId that = (ProductId) obj;
        return sellerId == that.sellerId && productId == that.productId;
    }
}
