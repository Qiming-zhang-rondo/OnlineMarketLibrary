package com.example.order.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customer_orders", schema = "order")
public class CustomerOrder {

    @Id
    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "next_order_id", nullable = false)
    private int nextOrderId;

  
    public CustomerOrder() {}

   
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getNextOrderId() {
        return nextOrderId;
    }

    public void setNextOrderId(int nextOrderId) {
        this.nextOrderId = nextOrderId;
    }
}
