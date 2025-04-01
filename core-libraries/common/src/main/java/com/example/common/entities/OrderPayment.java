package com.example.common.entities;

public class OrderPayment {

    private int orderId;

    // 1 - coupon, 2 - coupon, 3 - credit card
    private int paymentSequential;

    // coupon, credit card
    private PaymentType paymentType;

    // number of times the credit card is charged (usually once a month)
    private int paymentInstallments;

    // respective to this line (i.e., coupon)
    private float paymentValue;

    
    public OrderPayment() {}

    
    public OrderPayment(int orderId, int paymentSequential, PaymentType paymentType, int paymentInstallments, float paymentValue) {
        this.orderId = orderId;
        this.paymentSequential = paymentSequential;
        this.paymentType = paymentType;
        this.paymentInstallments = paymentInstallments;
        this.paymentValue = paymentValue;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getPaymentSequential() {
        return paymentSequential;
    }

    public void setPaymentSequential(int paymentSequential) {
        this.paymentSequential = paymentSequential;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public int getPaymentInstallments() {
        return paymentInstallments;
    }

    public void setPaymentInstallments(int paymentInstallments) {
        this.paymentInstallments = paymentInstallments;
    }

    public float getPaymentValue() {
        return paymentValue;
    }

    public void setPaymentValue(float paymentValue) {
        this.paymentValue = paymentValue;
    }

    // override toString() 
    @Override
    public String toString() {
        return "OrderPayment [orderId=" + orderId + ", paymentSequential=" + paymentSequential +
               ", paymentType=" + paymentType + ", paymentInstallments=" + paymentInstallments +
               ", paymentValue=" + paymentValue + "]";
    }
}
