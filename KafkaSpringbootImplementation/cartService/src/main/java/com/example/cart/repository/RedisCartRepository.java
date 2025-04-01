package com.example.cart.repository;

import com.example.cart.model.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisCartRepository implements ICartRepository {

    private static final String CART_PREFIX = "cart:";

    @Autowired
    private RedisTemplate<String, Cart> cartRedisTemplate;

    @Override
    public Cart findByCustomerId(int customerId) {
        return cartRedisTemplate.opsForValue().get(CART_PREFIX + customerId);
    }

    @Override
    public void saveCart(Cart cart) {
        cartRedisTemplate.opsForValue().set(CART_PREFIX + cart.getCustomerId(), cart, 30, TimeUnit.MINUTES);
    }

    @Override
    public void deleteCart(int customerId) {
        cartRedisTemplate.delete(CART_PREFIX + customerId);
    }

    @Override
    public void deleteAll() {
        Set<String> keys = cartRedisTemplate.keys(CART_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            cartRedisTemplate.delete(keys);
        }
    }
} 