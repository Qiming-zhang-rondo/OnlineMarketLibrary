package com.example.order.repository;

import com.example.order.model.Order;
import com.example.order.model.OrderId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RedisOrderRepository implements IOrderRepository {
    private static final String ORDER_PREFIX = "order:";
    private static final String ORDER_KEYS_PREFIX = "orderKeys:";

    @Autowired
    private RedisTemplate<String, Order> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String key(OrderId id) {
        return ORDER_PREFIX + id.getCustomerId() + ":" + id.getOrderId();
    }

    private String keySet(int customerId) {
        return ORDER_KEYS_PREFIX + customerId;
    }

    @Override
    public Optional<Order> findByCustomerIdAndOrderId(int customerId, int orderId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(new OrderId(customerId, orderId))));
    }

    @Override
    public List<Order> findByCustomerId(int customerId) {
        Set<String> keys = stringRedisTemplate.opsForSet().members(keySet(customerId));
        if (keys == null) return List.of();
        return keys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Order order) {
        String k = key(order.getId());
        redisTemplate.opsForValue().set(k, order);
        stringRedisTemplate.opsForSet().add(keySet(order.getCustomerId()), k);
    }


    @Override
    public void saveAll(List<Order> orders) {
        for (Order order : orders) {
            save(order); 
        }
    }

    @Override
    public void deleteAll() {
        Set<String> sets = stringRedisTemplate.keys(ORDER_KEYS_PREFIX + "*");
        if (sets != null) {
            for (String set : sets) {
                Set<String> members = stringRedisTemplate.opsForSet().members(set);
                if (members != null) redisTemplate.delete(members);
                stringRedisTemplate.delete(set);
            }
        }
    }


}