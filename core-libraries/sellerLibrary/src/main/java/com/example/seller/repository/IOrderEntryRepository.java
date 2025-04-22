package com.example.seller.repository;

import com.example.seller.model.OrderEntry;
import com.example.seller.model.OrderEntryId;
import com.example.common.entities.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface IOrderEntryRepository {
    Optional<OrderEntry> findById(OrderEntryId id);

    List<OrderEntry> findByCustomerIdAndOrderId(int customerId, int orderId);

    List<OrderEntry> findAllBySellerId(int sellerId);

    List<Object[]> findAllSellerAggregates(List<OrderStatus> statuses);

    void saveOrderEntry(OrderEntry orderEntry);

    void deleteOrderEntry(OrderEntry orderEntry);

    void deleteAll();

    void saveAll(List<OrderEntry> orderEntries);

    void save(OrderEntry orderEntry);
}
