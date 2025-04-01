package com.example.order.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items", schema = "order")
public class OrderItem {

    @EmbeddedId
    private OrderItemId id;  // composite key

    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(name = "product_name", nullable = false)
    private String productName = "";

    @Column(name = "seller_id", nullable = false)
    private int sellerId;

    @Column(name = "unit_price", nullable = false)
    private float unitPrice;

    @Column(name = "shipping_limit_date", nullable = false)
    private LocalDateTime shippingLimitDate;

    @Column(name = "freight_value", nullable = false)
    private float freightValue;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "total_items", nullable = false)
    private float totalItems;

    @Column(name = "total_amount", nullable = false)
    private float totalAmount;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", insertable = false, updatable = false),
        @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false)
    })
    private Order order;  // Many-to-one relationship with Order

    public OrderItem() {}

    // Getters and Setters
    public OrderItemId getId() {
        return id;
    }

    public void setId(OrderItemId id) {
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

    public int getOrderItemId(){
        return id.getOrderItemId();
    }

    public void setOrderItemId(int orderItemId){
        this.id.setOrderItemId(orderItemId);
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
