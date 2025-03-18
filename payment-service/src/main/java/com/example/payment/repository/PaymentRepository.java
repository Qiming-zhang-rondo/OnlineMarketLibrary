package com.example.payment.repository;

import com.example.payment.model.OrderPayment;
import com.example.payment.model.OrderPaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<OrderPayment, Integer> {

   
    @Query("SELECT p FROM OrderPayment p WHERE p.id.customerId = :customerId AND p.id.orderId = :orderId")
    List<OrderPayment> findAllByCustomerIdAndOrderId(@Param("customerId")int customerId, @Param("orderId")int orderId);



}
