package com.example.payment.repository;

import com.example.payment.model.OrderPaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderPaymentCardRepository extends JpaRepository<OrderPaymentCard, Integer> {

}
