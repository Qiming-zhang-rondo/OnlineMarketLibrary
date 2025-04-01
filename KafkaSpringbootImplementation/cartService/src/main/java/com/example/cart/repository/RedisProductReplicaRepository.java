package com.example.cart.repository;

import com.example.cart.model.ProductReplica;
import com.example.cart.model.ProductReplicaId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisProductReplicaRepository implements IProductReplicaRepository {

    private static final String PRODUCT_REPLICA_PREFIX = "productReplica:";

    @Autowired
    private RedisTemplate<String, ProductReplica> redisTemplate;

    private String key(ProductReplicaId id) {
        return PRODUCT_REPLICA_PREFIX + id.getSellerId() + ":" + id.getProductId();
    }

    @Override
    public ProductReplica findByProductReplicaId(ProductReplicaId productReplicaId) {
        return redisTemplate.opsForValue().get(key(productReplicaId));
    }

    @Override
    public void saveProductReplica(ProductReplica productReplica) {
        redisTemplate.opsForValue().set(
            key(productReplica.getProductReplicaId()),
            productReplica,
            10, TimeUnit.MINUTES
        );
    }

    @Override
    public void reset() {
        Set<String> keys = redisTemplate.keys(PRODUCT_REPLICA_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public void deleteAll() {
        Set<String> keys = redisTemplate.keys(PRODUCT_REPLICA_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
} 
