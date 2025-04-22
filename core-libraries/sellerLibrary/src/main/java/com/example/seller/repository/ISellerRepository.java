package com.example.seller.repository;

import com.example.seller.dto.SellerDashboard;
import com.example.seller.model.OrderEntry;
import com.example.seller.model.Seller;

import java.util.List;

public interface ISellerRepository {

    List<OrderEntry> findByCustomerIdAndOrderId(int customerId, int orderId);

    OrderEntry findById(int id);

    SellerDashboard queryDashboard(int sellerId);

    void deleteAllSellers();

    void deleteAllOrderEntries();

    void deleteAll();

    void save(Seller seller);

    void deleteById(int sellerId);
}
