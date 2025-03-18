package com.example.common.events;

public class PriceUpdate {

    private int sellerId;
    private int productId;
    private float price;
    private String version = "0";
    private String instanceId;

    // Default constructor
    public PriceUpdate() {}

    public PriceUpdate(int sellerId, int productId, float price, String version, String instanceId) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.price = price;
        this.version = (version != null) ? version : "0"; 
        this.instanceId = instanceId;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
