package com.example.order.repository;

import com.example.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    // Query order entries based on customerId and orderId
    @Query("SELECT oi FROM OrderItem oi WHERE oi.id.customerId = :customerId AND oi.id.orderId = :orderId")
    List<OrderItem> findByCustomerIdAndOrderId(@Param("customerId") int customerId, @Param("orderId") int orderId);
}
