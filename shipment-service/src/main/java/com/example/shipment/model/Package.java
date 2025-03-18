package com.example.shipment.model;

import com.example.common.entities.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "packages", schema = "shipment")
public class Package {

    @EmbeddedId
    private PackageId id;

    @Column(name = "seller_id", nullable = false)
    private int sellerId;

    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "freight_value", nullable = false)
    private float freightValue;

    @Column(name = "shipping_date", nullable = false)
    private LocalDateTime shippingDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PackageStatus status;  // Defined in common.entities

    @ManyToOne
@JoinColumns({
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", insertable = false, updatable = false),
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false)
})
private Shipment shipment;

    // Default constructor
    public Package() {}

    // Getters and setters for all fields
    public PackageId getId() {
        return id;
    }

    public void setId(PackageId id) {
        this.id = id;
    }

    public int getCustomerId(){
        return this.id.getCustomerId();
    }
    public void setCustomerId(int customerId){
        this.id.setCustomerId(customerId);
    }
    public int getOrderId(){
        return this.id.getOrderId();
    }
    public void setOrderId(int orderId){
        this.id.setOrderId(orderId);
    }
    public int getPackageId(){
        return this.id.getPackageId();
    }
    public void setPackageId(int packageId){
        this.id.setPackageId(packageId);
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getFreightValue() {
        return freightValue;
    }

    public void setFreightValue(float freightValue) {
        this.freightValue = freightValue;
    }

    public LocalDateTime getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDateTime shippingDate) {
        this.shippingDate = shippingDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }

    // Additional method to format OrderId as a string
    public String getOrderIdAsString() {
        return id.getCustomerId() + "|" + id.getOrderId();
    }
    public Shipment getShipment(){
        return shipment;
    }

    public void setShipment(Shipment shipment){
        this.shipment=shipment;
    }
}
