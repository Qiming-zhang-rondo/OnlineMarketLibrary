package com.example.payment.repository;

import com.example.payment.model.OrderPayment;

import java.util.List;

public interface IPaymentRepository {
    List<OrderPayment> findAllByCustomerIdAndOrderId(int customerId, int orderId);

    void save(OrderPayment payment);

    void saveAll(List<OrderPayment> payments);

    void deleteAll();

}