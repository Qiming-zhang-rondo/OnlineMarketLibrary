package com.example.cart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "replica_product", schema = "cartdb") 
@Getter
@Setter
public class ProductReplica {

    @EmbeddedId
    private ProductReplicaId productReplicaId;

    private String name;
    private float price;
    private String version;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public ProductReplica(ProductReplicaId productReplicaId){
        this.productReplicaId = productReplicaId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public ProductReplica() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }


    public int getSellerId() {
        return this.productReplicaId.getSellerId();
    }

    public void setSellerId(int sellerId) {
        if (this.productReplicaId == null) {
            this.productReplicaId = new ProductReplicaId();
        }
        this.productReplicaId.setSellerId(sellerId);
    }

    public int getProductId() {
        return this.productReplicaId.getProductId();
    }

    public void setProductId(int productId) {
        if (this.productReplicaId == null) {
            this.productReplicaId = new ProductReplicaId();
        }
        this.productReplicaId.setProductId(productId);
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for price
    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    // Getter and Setter for version
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    // Getter and Setter for active
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Getter and Setter for createdAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getter and Setter for updatedAt
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
