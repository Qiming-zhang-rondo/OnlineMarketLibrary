package com.example.common.entities;

import java.time.LocalDateTime;

public class OrderItem {

    private int orderId;
    private int orderItemId;
    private int productId;
    private String productName;
    private int sellerId;

    // prices change over time
    private float unitPrice;
    private LocalDateTime shippingLimitDate;
    private float freightValue;

    // not present in olist
    private int quantity;

    // without freight value
    private float totalItems;

    // without freight value
    private float totalAmount;

    // voucher
    private float totalIncentive;

    public OrderItem() {
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId=" + orderId +
                ", sellerId=" + sellerId +
                ", productId=" + productId +
                '}';
    }

    // Getters and setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
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

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDateTime getShippingLimitDate() {
        return shippingLimitDate;
    }

    public void setShippingLimitDate(LocalDateTime shippingLimitDate) {
        this.shippingLimitDate = shippingLimitDate;
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

    public float getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(float totalItems) {
        this.totalItems = totalItems;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float getTotalIncentive() {
        return totalIncentive;
    }

    public void setTotalIncentive(float totalIncentive) {
        this.totalIncentive = totalIncentive;
    }
}
