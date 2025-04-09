package com.example.cart.repository;

import com.example.cart.model.CartItem;
import com.example.cart.model.CartItemId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RedisCartItemRepository implements ICartItemRepository {

    private static final String CART_ITEM_PREFIX = "cartItem:";
    private static final String CART_ITEM_KEYS_PREFIX = "cartItemKeys:"; // New prefix for key sets

    @Autowired
    private RedisTemplate<String, CartItem> redisTemplate;
    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    private String key(CartItemId id) {
        return CART_ITEM_PREFIX + id.getCustomerId() + ":" + id.getSellerId() + ":" + id.getProductId();
    }

    private String keySet(int customerId) {
        return CART_ITEM_KEYS_PREFIX + customerId;
    }

    @Override
    public Optional<CartItem> findById(CartItemId id) {
        CartItem item = redisTemplate.opsForValue().get(key(id));
        return Optional.ofNullable(item);
    }

    @Override
    public List<CartItem> findByCustomerId(int customerId) {
        Set<String> keys = stringRedisTemplate.opsForSet().members(keySet(customerId));
        if (keys == null || keys.isEmpty())
            return List.of();
        return keys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void saveCartItem(CartItem cartItem) {
        String itemKey = key(cartItem.getId());
        redisTemplate.opsForValue().set(itemKey, cartItem);
        stringRedisTemplate.opsForSet().add(keySet(cartItem.getId().getCustomerId()), itemKey);
    }

    @Override
    public void deleteByCustomerId(int customerId) {
        Set<String> keys = stringRedisTemplate.opsForSet().members(keySet(customerId));
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            stringRedisTemplate.delete(keySet(customerId));
        }
    }

    /**
     * only in test
     */
    @Override
    public List<CartItem> findBySellerIdAndProductId(int sellerId, int productId) {
        Set<String> keys = redisTemplate.keys(CART_ITEM_PREFIX + "*");
        if (keys == null)
            return List.of();

        return keys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(item -> item != null &&
                        item.getId().getSellerId() == sellerId &&
                        item.getId().getProductId() == productId)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(CartItem item) {
        String itemKey = key(item.getId());
        redisTemplate.delete(itemKey);
        stringRedisTemplate.opsForSet().remove(keySet(item.getId().getCustomerId()), itemKey);
    }

    @Override
    public void deleteAll() {
        Set<String> allKeySets = stringRedisTemplate.keys(CART_ITEM_KEYS_PREFIX + "*");
        if (allKeySets != null) {
            for (String keySet : allKeySets) {
                Set<String> itemKeys = stringRedisTemplate.opsForSet().members(keySet);
                if (itemKeys != null) {
                    redisTemplate.delete(itemKeys);
                }
                stringRedisTemplate.delete(keySet);
            }
        }
    }

    @Override
    public void saveAll(List<CartItem> items) {
        items.forEach(this::saveCartItem);
    }
}
