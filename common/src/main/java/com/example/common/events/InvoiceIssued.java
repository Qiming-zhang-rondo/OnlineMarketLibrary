package com.example.common.events;

import com.example.common.requests.CustomerCheckout;
import com.example.common.entities.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public class InvoiceIssued {

    private CustomerCheckout customer;
    private int orderId;
    private String invoiceNumber;
    private LocalDateTime issueDate;
    private float totalInvoice;
    private List<OrderItem> items;
    private String instanceId;

    // Default constructor
    public InvoiceIssued() {}

    // Parameterized constructor
    public InvoiceIssued(CustomerCheckout customer, int orderId, String invoiceNumber, LocalDateTime issueDate, float totalInvoice, List<OrderItem> items, String instanceId) {
        this.customer = customer;
        this.orderId = orderId;
        this.invoiceNumber = invoiceNumber;
        this.issueDate = issueDate;
        this.totalInvoice = totalInvoice;
        this.items = items;
        this.instanceId = instanceId;
    }

    // Getters and Setters
    public CustomerCheckout getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerCheckout customer) {
        this.customer = customer;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public float getTotalInvoice() {
        return totalInvoice;
    }

    public void setTotalInvoice(float totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    // Overriding toString method
    @Override
    public String toString() {
        return String.join(",", items.toString());
    }
}
