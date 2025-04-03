package com.example.product.repository;

import com.example.product.model.Product;
import com.example.product.model.ProductId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static io.lettuce.core.MigrateArgs.Builder.key;

@Repository
public class RedisProductRepository implements IProductRepository {

    private static final String PRODUCT_PREFIX = "product:";

    @Autowired
    private RedisTemplate<String, Product> productRedisTemplate;


    @Override
    public Optional<Product> findById(ProductId id) {
        Product product = productRedisTemplate.opsForValue().get(key(id));
        return Optional.ofNullable(product);
    }

    @Override
    public List<Product> findByIdSellerId(int sellerId) {
        // 假设产品 Key 格式为 "product:{sellerId}:{productId}"
        Set<String> keys = productRedisTemplate.keys(PRODUCT_PREFIX + sellerId + ":*");
        if (keys != null && !keys.isEmpty()) {
            List<Product> products = productRedisTemplate.opsForValue().multiGet(keys);
            return products != null ? products : Collections.emptyList();
        }
        return Collections.emptyList();
    }

    @Override
    public void reset() {
        // 遍历所有产品，将状态置为 ACTIVE、版本号置为 0
        Set<String> keys = productRedisTemplate.keys(PRODUCT_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Product product = productRedisTemplate.opsForValue().get(key);
                if (product != null) {
                    product.setStatus("ACTIVE");
                    product.setVersion("0");
                    productRedisTemplate.opsForValue().set(key, product);
                }
            }
        }
    }

    @Override
    public void cleanup() {
        Set<String> keys = productRedisTemplate.keys(PRODUCT_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            productRedisTemplate.delete(keys);
        }
    }

    @Override
    public void deleteAll() {
        cleanup();
    }

    //?
    @Override
    public void saveProduct(Product product) {
        // 假设 ProductId 转换为字符串后格式为 "{sellerId}:{productId}"
        productRedisTemplate.opsForValue().set(
                PRODUCT_PREFIX + product.getProductId(),
                product,
                30, TimeUnit.MINUTES  // 可根据业务场景设置过期时间
        );
    }

    @Override
    public void saveAll(List<Product> products) {
        for (Product product : products) {
            saveProduct(product);
        }
    }


}
