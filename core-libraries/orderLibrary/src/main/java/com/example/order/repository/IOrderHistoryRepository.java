package com.example.order.repository;

import com.example.order.model.OrderHistory;

import java.util.List;

public interface IOrderHistoryRepository {

    List<OrderHistory> findByCustomerIdAndOrderId(int customerId, int orderId);

    void save(OrderHistory orderHistory);

    void saveAll(List<OrderHistory> historyList);

    void deleteAll();
}