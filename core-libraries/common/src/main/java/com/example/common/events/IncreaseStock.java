package com.example.common.events;

public class IncreaseStock {

    private int sellerId;
    private int productId;
    private int quantity;


    public IncreaseStock() {}


    public IncreaseStock(int sellerId, int productId, int quantity) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
