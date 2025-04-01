// package com.example.cart.repository;

// import com.example.cart.model.Cart;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// @Repository
// public interface JpaCartRepository extends JpaRepository<Cart, Integer>, AbstractCartRepository {
    
//     @Override
//     Cart findByCustomerId(int customerId);

//     @Override
//     default void saveCart(Cart cart) {
//         save(cart);  
//     }

//     @Override
//     default void deleteCart(int customerId) {
//         deleteById(customerId);  
//     }
// }