package com.example.customer.repository;

import com.example.customer.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class RedisCustomerRepository implements ICustomerRepository {

    private static final String CUSTOMER_KEY_PREFIX = "customer:";

    @Autowired
    private RedisTemplate<String, Customer> redisTemplate;

    private String key(int customerId) {
        return CUSTOMER_KEY_PREFIX + customerId;
    }

    @Override
    public Customer findById(int customerId) {
        return redisTemplate.opsForValue().get(key(customerId));
    }

    @Override
    public void save(Customer customer) {
        redisTemplate.opsForValue().set(key(customer.getId()), customer);
    }

    @Override
    public void deleteAll() {
        // 清除所有customer:前缀的key
        Set<String> keys = redisTemplate.keys(CUSTOMER_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public void reset() {
        Set<String> keys = redisTemplate.keys(CUSTOMER_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Customer customer = redisTemplate.opsForValue().get(key);
                if (customer != null) {
                    customer.setSuccessPaymentCount(0);
                    customer.setFailedPaymentCount(0);
                    redisTemplate.opsForValue().set(key, customer);
                }
            }
        }
    }
} 
