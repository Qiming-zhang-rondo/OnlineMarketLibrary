package com.example.order.repository;

import com.example.order.model.CustomerOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class RedisCustomerOrderRepository implements ICustomerOrderRepository {

    private static final String PREFIX = "customerOrder:";

    @Autowired
    private RedisTemplate<String, CustomerOrder> redisTemplate;

    @Override
    public CustomerOrder findByCustomerId(int customerId) {
        return redisTemplate.opsForValue().get(PREFIX + customerId);
    }

    @Override
    public void save(CustomerOrder customerOrder) {
        redisTemplate.opsForValue().set(PREFIX + customerOrder.getCustomerId(), customerOrder);
    }

    @Override
    public void deleteAll() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}