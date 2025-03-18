package com.example.order.repository;

import com.example.order.model.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Integer> {

    // Query order history based on customerId and orderId
    @Query("SELECT oh FROM OrderHistory oh WHERE oh.id.customerId = :customerId AND oh.id.orderId = :orderId")
    List<OrderHistory> findByCustomerIdAndOrderId(@Param("customerId") int customerId, @Param("orderId") int orderId);
}
