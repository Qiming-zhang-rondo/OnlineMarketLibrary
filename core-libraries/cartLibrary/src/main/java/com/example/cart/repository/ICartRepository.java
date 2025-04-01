package com.example.cart.repository;

import com.example.cart.model.Cart;

public interface ICartRepository {
    Cart findByCustomerId(int customerId);
    void saveCart(Cart cart);
    void deleteCart(int customerId);
    void deleteAll();

}