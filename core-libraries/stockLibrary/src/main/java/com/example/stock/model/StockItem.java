package com.example.stock.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_items", schema = "stock")
public class StockItem {

    @EmbeddedId
    private StockItemId id; 

    @Column(name = "qty_available")
    private int qtyAvailable;

    @Column(name = "qty_reserved")
    private int qtyReserved;

    @Column(name = "order_count")
    private int orderCount;

    @Column(name = "ytd")
    private int ytd; 

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "data")
    private String data;

    @Column(name = "version")
    private String version;

    @Column(name = "active")
    private boolean active;


    public StockItem() {}


    public StockItem(StockItemId id, int qtyAvailable, LocalDateTime createdAt) {
        this.id = id;
        this.qtyAvailable = qtyAvailable;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.active = true;
    }

    // Getters and Setters

    public StockItemId getId() {
        return id;
    }

    public void setId(StockItemId id) {
        this.id = id;
    }

    public int getSellerId(){
        return id.getSellerId();
    }

    public void setSellerId(int sellerId){
        this.id.setSellerId(sellerId);
    }

    public int getProductId(){
        return id.getProductId();
    }

    public void setProductId(int productId){
        this.id.setProductId(productId);
    }

    public int getQtyAvailable() {
        return qtyAvailable;
    }

    public void setQtyAvailable(int qtyAvailable) {
        this.qtyAvailable = qtyAvailable;
    }

    public int getQtyReserved() {
        return qtyReserved;
    }

    public void setQtyReserved(int qtyReserved) {
        this.qtyReserved = qtyReserved;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getYtd() {
        return ytd;
    }

    public void setYtd(int ytd) {
        this.ytd = ytd;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
