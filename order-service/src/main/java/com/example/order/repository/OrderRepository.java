package com.example.order.repository;

import com.example.order.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Query the order based on customerId, using a custom query
    @Query("SELECT o FROM Order o WHERE o.id.customerId = :customerId")
    List<Order> findByCustomerId(@Param("customerId") int customerId);

    // Query individual orders based on customerId and orderId
    @Query("SELECT o FROM Order o WHERE o.id.customerId = :customerId AND o.id.orderId = :orderId")
    Optional<Order> findByCustomerIdAndOrderId(@Param("customerId") int customerId, @Param("orderId") int orderId);
}
