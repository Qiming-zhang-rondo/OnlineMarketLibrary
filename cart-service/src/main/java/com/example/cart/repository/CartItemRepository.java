package com.example.cart.repository;

import com.example.cart.model.CartItem;
import com.example.cart.model.CartItemId;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {


    Optional<CartItem> findById(CartItemId id);

    @Query("SELECT c FROM CartItem c WHERE c.id.customerId = :customerId")
    List<CartItem> findByCustomerId(@Param("customerId") int customerId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.id.customerId = :customerId")
    void deleteByCustomerId(@Param("customerId") int customerId);

    @Query("SELECT c FROM CartItem c WHERE c.id.sellerId = :sellerId AND c.id.productId = :productId")
    List<CartItem> findBySellerIdAndProductId(@Param("sellerId") int sellerId, @Param("productId") int productId);

}
