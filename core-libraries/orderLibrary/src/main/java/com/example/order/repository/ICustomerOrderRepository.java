package com.example.order.repository;

import com.example.order.model.CustomerOrder;

public interface ICustomerOrderRepository {

    CustomerOrder findByCustomerId(int customerId);

    void save(CustomerOrder customerOrder);

    void deleteAll();
}