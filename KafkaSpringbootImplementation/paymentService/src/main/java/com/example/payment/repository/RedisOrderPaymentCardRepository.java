package com.example.payment.repository;

import com.example.payment.model.OrderPaymentCard;
import com.example.payment.model.OrderPaymentCardId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class RedisOrderPaymentCardRepository implements IOrderPaymentCardRepository {

    private final RedisTemplate<String, OrderPaymentCard> redisTemplate;
    private static final String PREFIX = "paymentCard:";

    public RedisOrderPaymentCardRepository(RedisTemplate<String, OrderPaymentCard> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(OrderPaymentCardId id) {
        return PREFIX + id.getCustomerId() + ":" + id.getOrderId() + ":" + id.getSequential();
    }

    @Override
    public void save(OrderPaymentCard card) {
        String redisKey = key(card.getId());
        redisTemplate.opsForValue().set(redisKey, card);
    }

    @Override
    public void deleteAll() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}