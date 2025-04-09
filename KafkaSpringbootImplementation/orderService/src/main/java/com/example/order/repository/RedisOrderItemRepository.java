package com.example.order.repository;

import com.example.order.model.OrderItem;
import com.example.order.model.OrderItemId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RedisOrderItemRepository implements IOrderItemRepository {
    private static final String PREFIX = "orderItem:";
    private static final String SET_PREFIX = "orderItemKeys:";

    @Autowired
    private RedisTemplate<String, OrderItem> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String key(OrderItemId id) {
        return PREFIX + id.getCustomerId() + ":" + id.getOrderId() + ":" + id.getOrderItemId();
    }

    private String keySet(int customerId, int orderId) {
        return SET_PREFIX + customerId + ":" + orderId;
    }

    @Override
    public List<OrderItem> findByCustomerIdAndOrderId(int customerId, int orderId) {
        Set<String> keys = stringRedisTemplate.opsForSet().members(keySet(customerId, orderId));
        if (keys == null) return List.of();
        return keys.stream().map(k -> redisTemplate.opsForValue().get(k)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void save(OrderItem item) {
        String k = key(item.getId());
        redisTemplate.opsForValue().set(k, item);
        stringRedisTemplate.opsForSet().add(keySet(item.getCustomerId(), item.getOrderId()), k);
    }

    @Override
    public void saveAll(List<OrderItem> items) {
        for (OrderItem item : items) {
            save(item); 
        }
    }

    @Override
    public void deleteAll() {
        Set<String> setKeys = stringRedisTemplate.keys(SET_PREFIX + "*");
        if (setKeys != null) {
            for (String setKey : setKeys) {
                Set<String> itemKeys = stringRedisTemplate.opsForSet().members(setKey);
                if (itemKeys != null && !itemKeys.isEmpty()) {
                    redisTemplate.delete(itemKeys); 
                }
                stringRedisTemplate.delete(setKey); 
            }
        }
    }
}
