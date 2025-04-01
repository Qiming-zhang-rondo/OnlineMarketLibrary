package com.example.cart.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_item", schema = "cartdb")
public class CartItem {

    @EmbeddedId
    private CartItemId id; 

    private String productName;
    private float unitPrice = 0;
    private float freightValue = 0;
    private int quantity = 1;
    private float voucher = 0;
    private String version;

    @ManyToOne
    @MapsId("customerId") 
    @JoinColumn(name = "customer_id") 
    private Cart cart;

   
    public CartItem() {
    }

    public CartItem(CartItemId id) {
        this.id = id;
    }

 
    public CartItemId getId() {
        return id;
    }

    public void setId(CartItemId id) {
        this.id = id;
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

    public int getSellerId() {
        return this.id.getSellerId();
    }

    public void setSellerId(int sellerId) {
        this.id.setSellerId(sellerId);
    }

    public int getProductId() {
        return this.id.getProductId(); 
    }

    public void setProductId(int productId) {
        this.id.setProductId(productId); 
    }

 
    public void setCart(Cart cart) {
        this.cart = cart;
        this.id.setCustomerId(cart.getCustomerId()); 
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", unitPrice=" + unitPrice +
                ", freightValue=" + freightValue +
                ", quantity=" + quantity +
                ", voucher=" + voucher +
                ", version='" + version + '\'' +
                ", cartCustomerId=" + (cart != null ? cart.getCustomerId() : "null") +
                '}';
    }

}
