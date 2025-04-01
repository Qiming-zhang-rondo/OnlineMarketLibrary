package com.example.shipment.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.common.entities.ShipmentStatus;

@Entity
@Table(name = "shipments", schema = "shipment")
public class Shipment {

    @EmbeddedId
    private ShipmentId id;

    @Column(name = "package_count", nullable = false)
    private int packageCount;

    @Column(name = "total_freight_value", nullable = false)
    private float totalFreightValue;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status; 

    // Customer details
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "complement")
    private String complement;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    // Relationship with Package entity
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Package> packages = new ArrayList<>();


    // Default constructor
    public Shipment() {}

    // Getters and setters for all fields
    public ShipmentId getId() {
        return id;
    }

    public void setId(ShipmentId id) {
        this.id = id;
    }

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

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public float getTotalFreightValue() {
        return totalFreightValue;
    }

    public void setTotalFreightValue(float totalFreightValue) {
        this.totalFreightValue = totalFreightValue;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }
}
