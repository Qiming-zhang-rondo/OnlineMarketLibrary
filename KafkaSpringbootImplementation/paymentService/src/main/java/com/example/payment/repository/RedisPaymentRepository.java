package com.example.payment.repository;

import com.example.payment.model.OrderPayment;
import com.example.payment.model.OrderPaymentId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RedisPaymentRepository implements IPaymentRepository {

    private final RedisTemplate<String, OrderPayment> redisTemplate;
    private static final String PREFIX = "payment:";

    public RedisPaymentRepository(RedisTemplate<String, OrderPayment> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(int customerId, int orderId) {
        return PREFIX + customerId + ":" + orderId;
    }

    @Override
    public void save(OrderPayment payment) {
        OrderPaymentId id = payment.getId();
        String redisKey = key(id.getCustomerId(), id.getOrderId());
        redisTemplate.opsForList().rightPush(redisKey, payment);
    }

    @Override
    public void saveAll(List<OrderPayment> payments) {
        if (payments == null || payments.isEmpty()) return;
        OrderPaymentId id = payments.get(0).getId();
        String redisKey = key(id.getCustomerId(), id.getOrderId());
        redisTemplate.opsForList().rightPushAll(redisKey, payments);
    }

    @Override
    public List<OrderPayment> findAllByCustomerIdAndOrderId(int customerId, int orderId) {
        String redisKey = key(customerId, orderId);
        return redisTemplate.opsForList().range(redisKey, 0, -1);
    }

    @Override
    public void deleteAll() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}