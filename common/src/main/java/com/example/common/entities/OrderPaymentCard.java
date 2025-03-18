package com.example.common.entities;

public class OrderPaymentCard {

 
    private int orderId;
    private int paymentSequential;


    private String cardNumber = "";
    private String cardHolderName = "";
    private String cardExpiration = "";
    // private String cardSecurityNumber; 

    private String cardBrand = "";

  
    public OrderPaymentCard() {}

   
    public OrderPaymentCard(int orderId, int paymentSequential, String cardNumber, String cardHolderName, String cardExpiration, String cardBrand) {
        this.orderId = orderId;
        this.paymentSequential = paymentSequential;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.cardExpiration = cardExpiration;
        this.cardBrand = cardBrand;
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

    public String getCardExpiration() {
        return cardExpiration;
    }

    public void setCardExpiration(String cardExpiration) {
        this.cardExpiration = cardExpiration;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }


    @Override
    public String toString() {
        return "OrderPaymentCard [orderId=" + orderId + ", paymentSequential=" + paymentSequential + 
               ", cardNumber=" + cardNumber + ", cardHolderName=" + cardHolderName + 
               ", cardExpiration=" + cardExpiration + ", cardBrand=" + cardBrand + "]";
    }
}
