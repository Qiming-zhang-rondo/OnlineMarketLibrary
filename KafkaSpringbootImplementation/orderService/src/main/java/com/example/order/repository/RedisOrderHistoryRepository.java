package com.example.order.repository;

import com.example.order.model.OrderHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RedisOrderHistoryRepository implements IOrderHistoryRepository {

    private static final String PREFIX = "orderHistory:";
    private static final String SET_PREFIX = "orderHistoryKeys:";

    @Autowired
    private RedisTemplate<String, OrderHistory> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String key(OrderHistory history) {
        return PREFIX + history.getCustomerId() + ":" + history.getOrderId() + ":" + history.getStatus();
    }

    private String keySet(int customerId, int orderId) {
        return SET_PREFIX + customerId + ":" + orderId;
    }

    @Override
    public List<OrderHistory> findByCustomerIdAndOrderId(int customerId, int orderId) {
        Set<String> keys = stringRedisTemplate.opsForSet().members(keySet(customerId, orderId));
        if (keys == null || keys.isEmpty()) return List.of();
        return keys.stream()
                .map(k -> redisTemplate.opsForValue().get(k))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void save(OrderHistory history) {
        String k = key(history);
        redisTemplate.opsForValue().set(k, history);
        stringRedisTemplate.opsForSet().add(keySet(history.getCustomerId(), history.getOrderId()), k);
    }

    @Override
    public void saveAll(List<OrderHistory> historyList) {
        for (OrderHistory history : historyList) {
            save(history);
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