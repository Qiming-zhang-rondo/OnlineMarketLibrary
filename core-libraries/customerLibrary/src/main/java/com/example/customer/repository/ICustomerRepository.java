package com.example.customer.repository;

import com.example.customer.model.Customer;

public interface ICustomerRepository {
    Customer findById(int customerId);
    void save(Customer customer);
    void deleteAll();
    void reset();
}