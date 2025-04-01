package com.example.order.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.example.common.entities.OrderStatus;

@Entity
@Table(name = "order_history", schema = "order")
public class OrderHistory {

    @EmbeddedId
    private OrderHistoryId id;  // composite key

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", insertable = false, updatable = false),
        @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false)
    })
    private Order order;  //Many-to-one relationship with Order

    public OrderHistory() {}

    // Getters and Setters
    public OrderHistoryId getId() {
        return id;
    }

    public void setId(OrderHistoryId id) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
