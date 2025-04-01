package com.example.cart.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.common.entities.CartStatus;

@Entity
@Table(name = "cart", schema = "cartdb")
public class Cart {

    @Id
    private int customerId;

    @Enumerated(EnumType.STRING)
    private CartStatus status = CartStatus.OPEN;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();;

    public Cart() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
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

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("CustomerId: ").append(customerId)
                .append(", Status: ").append(status.toString())
                .append(", CreatedAt: ").append(createdAt)
                .append(", UpdatedAt: ").append(updatedAt)
                .toString();
    }
}
