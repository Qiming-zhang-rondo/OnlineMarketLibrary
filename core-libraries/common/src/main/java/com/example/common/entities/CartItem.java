package com.example.common.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CartItem {
    @JsonProperty("SellerId")
    private int sellerId;
    @JsonProperty("ProductId")
    private int productId;
    @JsonProperty("ProductName")
    private String productName = "";
    @JsonProperty("UnitPrice")
    private float unitPrice;
    @JsonProperty("FreightValue")
    private float freightValue;
    @JsonProperty("Quantity")
    private int quantity;
    @JsonProperty("Voucher")
    private float voucher;
    @JsonProperty("Version")
    private String version;

    
    public CartItem() {}

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public float getFreightValue() {
        return freightValue;
    }

    public void setFreightValue(float freightValue) {
        this.freightValue = freightValue;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getVoucher() {
        return voucher;
    }

    public void setVoucher(float voucher) {
        this.voucher = voucher;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

   
    @Override
    public String toString() {
        return "SellerId: " + sellerId + ", ProductId: " + productId + ", Quantity: " + quantity + ", Version: " + version;
    }
}
