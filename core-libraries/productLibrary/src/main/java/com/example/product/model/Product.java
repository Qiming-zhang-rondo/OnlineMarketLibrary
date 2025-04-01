package com.example.product.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;



@Entity
@Table(name = "product", schema = "productdb") 
public class Product {

    @EmbeddedId
    private ProductId id;  // using composite key

    private String name = "";
    private String sku = "";
    private String category = "";
    private String description = "";
    private float price;
    private float freightValue;
    private String status = "approved";
    private String version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public Product() {}

    // Constructor with fields and version
    public Product(Product product, String version) {
        this.id = product.id;
        this.name = product.name;
        this.sku = product.sku;
        this.category = product.category;
        this.description = product.description;
        this.price = product.price;
        this.freightValue = product.freightValue;
        this.status = product.status;
        this.version = version;
    }

    // Constructor with new price
    public Product(Product product, float newPrice) {
        this.id = product.id;
        this.name = product.name;
        this.sku = product.sku;
        this.category = product.category;
        this.description = product.description;
        this.price = newPrice;
        this.freightValue = product.freightValue;
        this.status = product.status;
        this.version = product.version;
    }

    // Lifecycle callbacks to manage createdAt and updatedAt
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public ProductId getId() {
        return id;
    }

    public void setId(ProductId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getFreightValue() {
        return freightValue;
    }

    public void setFreightValue(float freightValue) {
        this.freightValue = freightValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getSellerId() {
            return this.id.getSellerId();
    }
    
    public void setSellerId(int sellerId) {
        this.id.setSellerId(sellerId);
    }
    
    public int getProductId() {
        
        return this.id.getProductId();
        
    }
    
    public void setProductId(int productId) {
        this.id.setProductId(productId);
    }
    
    
}
