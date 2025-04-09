package com.example.payment.repository;

import com.example.payment.model.OrderPaymentCard;

public interface IOrderPaymentCardRepository {
    void save(OrderPaymentCard card);

    void deleteAll();
}