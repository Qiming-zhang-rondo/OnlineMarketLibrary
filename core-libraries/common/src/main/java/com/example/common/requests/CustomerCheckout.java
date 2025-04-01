package com.example.common.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A sub-type of customer.
 * Ideally, address and credit card info may change across customer checkouts.
 * Basket and Order does not need to know all internal data about customers.
 */
public class CustomerCheckout {

   @JsonProperty("CustomerId")
    private int customerId;

    @JsonProperty("FirstName")
    private String firstName;

    @JsonProperty("LastName")
    private String lastName;

    @JsonProperty("Street")
    private String street;

    @JsonProperty("Complement")
    private String complement;

    @JsonProperty("City")
    private String city;

    @JsonProperty("State")
    private String state;

    @JsonProperty("ZipCode")
    private String zipCode;

    @JsonProperty("PaymentType")
    private String paymentType;

    @JsonProperty("CardNumber")
    private String cardNumber;

    @JsonProperty("CardHolderName")
    private String cardHolderName;

    @JsonProperty("CardExpiration")
    private String cardExpiration;

    @JsonProperty("CardSecurityNumber")
    private String cardSecurityNumber;

    @JsonProperty("CardBrand")
    private String cardBrand;

    @JsonProperty("Installments")
    private int installments;

    @JsonProperty("instanceId")
    private String instanceId;

    public CustomerCheckout() {
    }

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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

    public String getCardSecurityNumber() {
        return cardSecurityNumber;
    }

    public void setCardSecurityNumber(String cardSecurityNumber) {
        this.cardSecurityNumber = cardSecurityNumber;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public int getInstallments() {
        return installments;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String toString() {
        return "CustomerCheckout{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", street='" + street + '\'' +
                ", complement='" + complement + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", cardExpiration='" + cardExpiration + '\'' +
                ", cardSecurityNumber='" + cardSecurityNumber + '\'' +
                ", cardBrand='" + cardBrand + '\'' +
                ", installments=" + installments +
                ", instanceId='" + instanceId + '\'' +
                '}';
    }
}
