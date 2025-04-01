package com.example.seller.model;

import com.example.common.entities.OrderStatus;
import com.example.common.entities.PackageStatus;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_entries", schema = "seller")
public class OrderEntry {

    @EmbeddedId
    private OrderEntryId id;  // using composite key

    @Column(name = "natural_key")
    private String naturalKey;

    private Integer packageId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "unit_price")
    private float unitPrice;

    private int quantity;

    @Column(name = "total_items")
    private float totalItems;

    @Column(name = "total_amount")
    private float totalAmount;

    @Column(name = "total_incentive")
    private float totalIncentive;

    @Column(name = "total_invoice")
    private float totalInvoice;

    @Column(name = "freight_value")
    private float freightValue;

    @Column(name = "shipment_date")
    private LocalDateTime shipmentDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private PackageStatus deliveryStatus;


    public OrderEntry() {}

    // Getter and Setter 

    public OrderEntryId getId() {
        return id;
    }

    public void setId(OrderEntryId id) {
        this.id = id;
    }

    public void setCustomerId(int customerId){
        this.id.setCustomerId(customerId);
    }

    public int getCustomerId(){
        return id.getCustomerId();
    }

    public int getOrderId() {
        return id.getOrderId();
    }

    public void setOrderId(int orderId) {
        this.id.setOrderId(orderId);
    }

    public int getSellerId() {
        return id.getSellerId();
    }

    public void setSellerId(int sellerId) {
        this.id.setSellerId(sellerId);
    }

    public int getProductId() {
        return id.getProductId();
    }

    public void setProductId(int productId) {
        this.id.setProductId(productId);
    }

    public String getNaturalKey() {
        return naturalKey;
    }

    public void setNaturalKey(String naturalKey) {
        this.naturalKey = naturalKey;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
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

    public float getTotalInvoice() {
        return totalInvoice;
    }

    public void setTotalInvoice(float totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    public float getFreightValue() {
        return freightValue;
    }

    public void setFreightValue(float freightValue) {
        this.freightValue = freightValue;
    }

    public LocalDateTime getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(LocalDateTime shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PackageStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(PackageStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
