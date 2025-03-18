package com.example.payment.model;

import jakarta.persistence.*;
import com.example.common.entities.*;
import com.example.common.integration.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_payments", schema = "payment")
public class OrderPayment {

    @EmbeddedId
    private OrderPaymentId id;  // using composite key

    @Column(name = "installments", nullable = false)
    private int installments;

    @Column(name = "value", nullable = false)
    private float value;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "orderPayment", cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderPaymentCard orderPaymentCard;

    public OrderPayment() {}

    public OrderPayment(OrderPaymentId id, int installments, float value, PaymentType type, PaymentStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.installments = installments;
        this.value = value;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getter and Setter 

    public OrderPaymentId getId() {
        return id;
    }

    public void setId(OrderPaymentId id) {
        this.id = id;
    }

    public int getCustomerId() {
        return id.getCustomerId();  // get customerId from composite key
    }

    public void setCustomerId(int customerId) {
        if (this.id == null) {
            this.id = new OrderPaymentId();
        }
        this.id.setCustomerId(customerId);  // set customerId
    }

    public int getOrderId() {
        return id.getOrderId();  // get orderId from composite key
    }

    public void setOrderId(int orderId) {
        if (this.id == null) {
            this.id = new OrderPaymentId();
        }
        this.id.setOrderId(orderId);  // set orderId
    }

    public int getSequential() {
        return id.getSequential();  // get sequential from composite
    }

    public void setSequential(int sequential) {
        if (this.id == null) {
            this.id = new OrderPaymentId();
        }
        this.id.setSequential(sequential);  // set sequential
    }

    public int getInstallments() {
        return installments;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OrderPaymentCard getOrderPaymentCard() {
        return orderPaymentCard;
    }

    public void setOrderPaymentCard(OrderPaymentCard orderPaymentCard) {
        this.orderPaymentCard = orderPaymentCard;
    }
}
