// package com.example.cart.repository;

// import com.example.cart.model.CartItem;
// import com.example.cart.model.CartItemId;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Modifying;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface JpaCartItemRepository extends JpaRepository<CartItem, CartItemId>, AbstractCartItemRepository {

//     @Override
//     Optional<CartItem> findById(CartItemId id);

//     @Override
//     @Query("SELECT c FROM CartItem c WHERE c.id.customerId = :customerId")
//     List<CartItem> findByCustomerId(@Param("customerId") int customerId);

//     @Override
//     default void saveCartItem(CartItem cartItem) {
//         save(cartItem);
//     }

//     @Override
//     @Modifying
//     @Query("DELETE FROM CartItem c WHERE c.id.customerId = :customerId")
//     void deleteByCustomerId(@Param("customerId") int customerId);

//     @Override
//     @Query("SELECT c FROM CartItem c WHERE c.id.sellerId = :sellerId AND c.id.productId = :productId")
//     List<CartItem> findBySellerIdAndProductId(@Param("sellerId") int sellerId, @Param("productId") int productId);
// }