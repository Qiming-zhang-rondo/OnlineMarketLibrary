package com.example.common.integration;

public class PaymentIntent {

    // example: pi_1GszdL2eZvKYlo2C4nORvwio
    private String id = "";
    
    private float amount;

    // example: pi_1GszdL2eZvKYlo2C4nORvwio_secret_F06b3J3jgLq8Ueo5JeZUF79mr
    private String clientSecret = "";

    private String status = PaymentStatus.SUCCEEDED.toString();

    private String currency = "";

    private String customer = "";

    private String confirmationMethod = "automatic";

    private int created;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getConfirmationMethod() {
        return confirmationMethod;
    }

    public void setConfirmationMethod(String confirmationMethod) {
        this.confirmationMethod = confirmationMethod;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }
}
