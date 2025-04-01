package com.example.common.entities;

public class ProductStatus {

    private int id;
    private ItemStatus status;
    private float unitPrice = 0;
    private float oldUnitPrice = 0;
    private int qtyAvailable = 0;


    public ProductStatus() {}


    public ProductStatus(int id, ItemStatus status, float unitPrice, float oldUnitPrice) {
        this.id = id;
        this.status = status;
        this.unitPrice = unitPrice;
        this.oldUnitPrice = oldUnitPrice;
    }


    public ProductStatus(int id, ItemStatus status) {
        this.id = id;
        this.status = status;
    }


    public ProductStatus(int id, ItemStatus status, int qtyAvailable) {
        this.id = id;
        this.status = status;
        this.qtyAvailable = qtyAvailable;
    }

    // Getters 和 Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public float getOldUnitPrice() {
        return oldUnitPrice;
    }

    public void setOldUnitPrice(float oldUnitPrice) {
        this.oldUnitPrice = oldUnitPrice;
    }

    public int getQtyAvailable() {
        return qtyAvailable;
    }

    public void setQtyAvailable(int qtyAvailable) {
        this.qtyAvailable = qtyAvailable;
    }
}
