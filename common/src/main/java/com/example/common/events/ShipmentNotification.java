package com.example.common.events;

import com.example.common.entities.ShipmentStatus;

import java.time.LocalDateTime;

public class ShipmentNotification {
    private int customerId;
    private int orderId;
    private LocalDateTime eventDate;
    private String instanceId;
    private ShipmentStatus status;

    public ShipmentNotification() {}

    public ShipmentNotification(int customerId, int orderId, LocalDateTime eventDate, String instanceId, ShipmentStatus status) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.eventDate = eventDate;
        this.instanceId = instanceId;
        this.status = status;
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

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }
}
