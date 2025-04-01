package com.example.cart.repository;

import com.example.cart.model.CartItem;
import com.example.cart.model.CartItemId;

import java.util.List;
import java.util.Optional;

public interface ICartItemRepository {
    Optional<CartItem> findById(CartItemId id);
    List<CartItem> findByCustomerId(int customerId);
    void deleteByCustomerId(int customerId);
    List<CartItem> findBySellerIdAndProductId(int sellerId, int productId);
    void saveCartItem(CartItem cartItem);
    void delete(CartItem cartItem);
    void deleteAll();
    void saveAll(List<CartItem> items);
}