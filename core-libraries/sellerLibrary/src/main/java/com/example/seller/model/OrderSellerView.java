package com.example.seller.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * View
 * Overview of orders in progress
 */
@Entity
@Table(name = "order_seller_view")
public class OrderSellerView {
    @Id
    private int sellerId;

    // information below from a seller's perspective

    // order
    private int countOrders = 0;
    private int countItems = 0;
    private float totalAmount = 0;
    private float totalFreight = 0;

    private float totalIncentive = 0;

    private float totalInvoice = 0;
    private float totalItems = 0;

   
    public OrderSellerView() {}

    // Getter and Setter 
    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public int getCountOrders() {
        return countOrders;
    }

    public void setCountOrders(int countOrders) {
        this.countOrders = countOrders;
    }

    public int getCountItems() {
        return countItems;
    }

    public void setCountItems(int countItems) {
        this.countItems = countItems;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float getTotalFreight() {
        return totalFreight;
    }

    public void setTotalFreight(float totalFreight) {
        this.totalFreight = totalFreight;
    }

    public float getTotalIncentive() {
        return totalIncentive;
    }

    public void setTotalIncentive(float totalIncentive) {
        this.totalIncentive = totalIncentive;
    }

    public float getTotalInvoice() {
        return totalInvoice;
    }

    public void setTotalInvoice(float totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    public float getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(float totalItems) {
        this.totalItems = totalItems;
    }
}
