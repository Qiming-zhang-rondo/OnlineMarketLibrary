package com.example.common.integration;

public class PaymentIntentCreateOptions {

    private String customer = "";
    private float amount;
    private String paymentMethod = "";
    private String idempotencyKey = "";
    private CardOptions cardOptions;
    private String setupFutureUsage = "off_session";
    private Currency currency = Currency.USD;

   
     public PaymentIntentCreateOptions() {}

     public PaymentIntentCreateOptions(float amount, String customer, String idempotencyKey, String cardNumber, String cvc, int expMonth, int expYear) {
         this.amount = amount;
         this.customer = customer;
         this.idempotencyKey = idempotencyKey;
         this.cardOptions = new CardOptions();
         this.cardOptions.setNumber(cardNumber);
         this.cardOptions.setCvc(cvc);
         this.cardOptions.setExpMonth(String.valueOf(expMonth));
         this.cardOptions.setExpYear(String.valueOf(expYear));
     }

    // Getters and Setters
    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public CardOptions getCardOptions() {
        return cardOptions;
    }

    public void setCardOptions(CardOptions cardOptions) {
        this.cardOptions = cardOptions;
    }

    public String getSetupFutureUsage() {
        return setupFutureUsage;
    }

    public void setSetupFutureUsage(String setupFutureUsage) {
        this.setupFutureUsage = setupFutureUsage;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
