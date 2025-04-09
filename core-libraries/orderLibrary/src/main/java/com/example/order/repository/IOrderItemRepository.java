package com.example.order.repository;

import com.example.order.model.OrderItem;

import java.util.List;

public interface IOrderItemRepository {

    List<OrderItem> findByCustomerIdAndOrderId(int customerId, int orderId);

    void save(OrderItem orderItem);

    void saveAll(List<OrderItem> items);

    void deleteAll();
}