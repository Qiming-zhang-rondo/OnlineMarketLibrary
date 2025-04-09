package com.example.order.repository;

import com.example.order.model.Order;

import java.util.List;
import java.util.Optional;

public interface IOrderRepository {

    List<Order> findByCustomerId(int customerId);

    Optional<Order> findByCustomerIdAndOrderId(int customerId, int orderId);

    void save(Order order);

    void saveAll(List<Order> orders);

    void deleteAll();

}
