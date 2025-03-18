package com.example.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class StockItemId implements Serializable {
    @Column(name = "seller_id")
    @JsonProperty("seller_id")
    private int sellerId;

    @Column(name = "product_id")
    @JsonProperty("product_id")
    private int productId;

    public StockItemId() {}

    public StockItemId(int sellerId, int productId) {
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

    // hashCode and equals
    @Override
    public int hashCode() {
        return Objects.hash(sellerId, productId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StockItemId that = (StockItemId) obj;
        return sellerId == that.sellerId && productId == that.productId;
    }
}
