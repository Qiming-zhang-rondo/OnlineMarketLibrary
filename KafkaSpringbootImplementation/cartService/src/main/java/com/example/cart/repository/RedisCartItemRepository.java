package com.example.cart.repository;

import com.example.cart.model.CartItem;
import com.example.cart.model.CartItemId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class RedisCartItemRepository implements ICartItemRepository {

    private static final String CART_ITEM_PREFIX = "cartItem:";

    @Autowired
    private RedisTemplate<String, CartItem> redisTemplate;

    private String key(CartItemId id) {
        return CART_ITEM_PREFIX + id.getCustomerId() + ":" + id.getSellerId() + ":" + id.getProductId();
    }

    @Override
    public Optional<CartItem> findById(CartItemId id) {
        CartItem item = redisTemplate.opsForValue().get(key(id));
        return Optional.ofNullable(item);
    }

    @Override
    public List<CartItem> findByCustomerId(int customerId) {
        Set<String> keys = redisTemplate.keys(CART_ITEM_PREFIX + customerId + ":*");
        if (keys == null) return List.of();
        return keys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    @Override
    public void saveCartItem(CartItem cartItem) {
        redisTemplate.opsForValue().set(key(cartItem.getId()), cartItem);
    }

    @Override
    public void deleteByCustomerId(int customerId) {
        Set<String> keys = redisTemplate.keys(CART_ITEM_PREFIX + customerId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public List<CartItem> findBySellerIdAndProductId(int sellerId, int productId) {
        Set<String> keys = redisTemplate.keys(CART_ITEM_PREFIX + "*");
        if (keys == null) return List.of();
        return keys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(item -> item != null &&
                        item.getId().getSellerId() == sellerId &&
                        item.getId().getProductId() == productId)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(CartItem item) {
        redisTemplate.delete(key(item.getId()));
    }

    @Override
    public void deleteAll() {
        Set<String> keys = redisTemplate.keys(CART_ITEM_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public void saveAll(List<CartItem> items) {
        items.forEach(this::saveCartItem);
    }
} 
