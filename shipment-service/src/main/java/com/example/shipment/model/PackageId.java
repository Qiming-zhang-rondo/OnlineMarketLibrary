package com.example.shipment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PackageId implements Serializable {
    @Column(name = "customer_id", nullable = false)
    private int customerId;
    @Column(name = "order_id", nullable = false)
    private int orderId;
    @Column(name = "package_id", nullable = false)
    private int packageId;

    public PackageId() {}

    public PackageId(int customerId, int orderId, int packageId) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.packageId = packageId;
    }

    // Getters and Setters

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageId packageId1 = (PackageId) o;
        return customerId == packageId1.customerId &&
                orderId == packageId1.orderId &&
                packageId == packageId1.packageId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, orderId, packageId);
    }
}
