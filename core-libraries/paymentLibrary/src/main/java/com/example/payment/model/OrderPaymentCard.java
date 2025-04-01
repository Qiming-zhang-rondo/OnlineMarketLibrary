package com.example.payment.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_payment_cards", schema = "payment")
public class OrderPaymentCard {

    @EmbeddedId
    private OrderPaymentCardId id;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "card_holder_name", nullable = false)
    private String cardHolderName;

    @Column(name = "card_expiration", nullable = false)
    private LocalDateTime cardExpiration;

    @Column(name = "card_brand", nullable = false)
    private String cardBrand;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", insertable = false, updatable = false),
        @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false),
        @JoinColumn(name = "sequential", referencedColumnName = "sequential", insertable = false, updatable = false)
    })
    private OrderPayment orderPayment;

    public OrderPaymentCard() {
    }

    // Getters and setters for embedded ID fields
    public int getCustomerId() {
        return id.getCustomerId();
    }

    public void setCustomerId(int customerId) {
        this.id.setCustomerId(customerId);
    }

    public int getOrderId() {
        return id.getOrderId();
    }

    public void setOrderId(int orderId) {
        this.id.setOrderId(orderId);
    }

    public int getSequential() {
        return id.getSequential();
    }

    public void setSequential(int sequential) {
        this.id.setSequential(sequential);
    }

    // Other getters and setters
    public OrderPaymentCardId getId() {
        return id;
    }

    public void setId(OrderPaymentCardId id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public LocalDateTime getCardExpiration() {
        return cardExpiration;
    }

    public void setCardExpiration(LocalDateTime cardExpiration) {
        this.cardExpiration = cardExpiration;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public OrderPayment getOrderPayment() {
        return orderPayment;
    }

    public void setOrderPayment(OrderPayment orderPayment) {
        this.orderPayment = orderPayment;
    }
}
