package com.example.cart.model;
import java.util.Objects;

import lombok.Data;
import java.io.Serializable;
import jakarta.persistence.Embeddable;

@Data
@Embeddable
public class ProductReplicaId implements Serializable {

    private int sellerId;
    private int productId;


    public ProductReplicaId() {}


    public ProductReplicaId(int sellerId, int productId) {
        this.sellerId = sellerId;
        this.productId = productId;
    }

    // Getter and Setter for sellerId
    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    // Getter and Setter for productId
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
        ProductReplicaId that = (ProductReplicaId) o;
        return sellerId == that.sellerId && productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellerId, productId);
    }
}
