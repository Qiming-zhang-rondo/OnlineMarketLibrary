package com.example.order.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.common.entities.OrderStatus;

@Entity
@Table(name = "orders", schema = "order")
public class Order {

    @EmbeddedId
    private OrderId id;  // composit key

    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber = "";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.CREATED;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "delivered_carrier_date")
    private LocalDateTime deliveredCarrierDate;

    @Column(name = "delivered_customer_date")
    private LocalDateTime deliveredCustomerDate;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "count_items", nullable = false)
    private int countItems;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "total_amount", nullable = false)
    private float totalAmount = 0;

    @Column(name = "total_freight", nullable = false)
    private float totalFreight = 0;

    @Column(name = "total_incentive", nullable = false)
    private float totalIncentive = 0;

    @Column(name = "total_invoice", nullable = false)
    private float totalInvoice = 0;

    @Column(name = "total_items", nullable = false)
    private float totalItems = 0;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderHistory> history = new ArrayList<>();

    public Order() {}

    // Getters and Setters
    public OrderId getId() {
        return id;
    }

    public void setId(OrderId id) {
        this.id = id;
    }

    public int getOrderId(){
        return id.getOrderId();
    }

    public void setOrderId(int orderId){
        this.id.setOrderId(orderId);
    }

    public int getCustomerId(){
        return id.getCustomerId();
    }

    public void setCustomerId(int customerId){
        this.id.setCustomerId(customerId);
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getDeliveredCarrierDate() {
        return deliveredCarrierDate;
    }

    public void setDeliveredCarrierDate(LocalDateTime deliveredCarrierDate) {
        this.deliveredCarrierDate = deliveredCarrierDate;
    }

    public LocalDateTime getDeliveredCustomerDate() {
        return deliveredCustomerDate;
    }

    public void setDeliveredCustomerDate(LocalDateTime deliveredCustomerDate) {
        this.deliveredCustomerDate = deliveredCustomerDate;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public int getCountItems() {
        return countItems;
    }

    public void setCountItems(int countItems) {
        this.countItems = countItems;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public List<OrderHistory> getHistory() {
        return history;
    }

    public void setHistory(List<OrderHistory> history) {
        this.history = history;
    }
}
