package com.example.common.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
    @JsonProperty("seller_id")
    private int sellerId;
    @JsonProperty("product_id")
    private int productId;
    private String name;
    private String sku;
    private String category;
    private String description;
    private float price;
    @JsonProperty("freight_value")
    private float freightValue;
    private String status; // approved by default
    private String version;

    public Product() {
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

    @Override
    public String toString() {
        return String.format("sellerId: %d, productId: %d, version: %s", sellerId, productId, version);
    }
}
