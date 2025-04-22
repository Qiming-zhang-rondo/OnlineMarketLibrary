package com.example.seller.controller;

import com.example.seller.dto.SellerDashboard;
import com.example.seller.model.Seller;
import com.example.seller.service.ISellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private RedisTemplate<String, Seller> sellerRedisTemplate;

    @Autowired
    private ISellerService sellerService;

    private static final Logger logger = LoggerFactory.getLogger(SellerController.class);

    /**
     * 添加或更新卖家信息（仅使用 Redis 作为数据存储）
     */
    @PostMapping("/")
    public ResponseEntity<?> addSeller(@RequestBody Seller seller) {
        logger.info("Received add seller request: {}", seller);

        // Redis key 格式： seller:{sellerId}
        String redisKey = "seller:" + seller.getId();

        // Step 1: 尝试从 Redis 中获取 Seller
        Seller cachedSeller = sellerRedisTemplate.opsForValue().get(redisKey);
        if (cachedSeller != null) {
            // 如果已存在，则认为卖家信息已存在，可以选择更新或者拒绝重复添加
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Seller " + seller.getId() + " already exists.");
        }

        // 如果 Redis 中不存在，则认为是新增卖家，
        // 直接使用请求中的 seller 对象，并设置 sellerId
        seller.setId(seller.getId());

        // Step 2: 保存到 Redis，设置过期时间（例如 30 分钟）
        sellerRedisTemplate.opsForValue().set(redisKey, seller, 30, java.util.concurrent.TimeUnit.MINUTES);
        logger.info("Seller cached in Redis for seller {}", seller.getId());

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 获取卖家详情（只从 Redis 获取）
     */
    @GetMapping("/{sellerId}")
    public ResponseEntity<?> getSeller(@PathVariable int sellerId) {
        String redisKey = "seller:" + sellerId;
        Seller seller = sellerRedisTemplate.opsForValue().get(redisKey);
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Seller " + sellerId + " not found in Redis");
        }
        return ResponseEntity.ok(seller);
    }

    /**
     * 查询卖家仪表盘数据，委托给 SellerService（该服务可以仅基于 Redis 计算汇总数据）
     */
    @GetMapping("/dashboard/{sellerId}")
    public ResponseEntity<?> getDashboard(@PathVariable int sellerId) {
        logger.info("Received dashboard request for seller: {}", sellerId);
        try {
            SellerDashboard dashboard = sellerService.queryDashboard(sellerId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("Error querying dashboard for seller {}: {}", sellerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     * 删除卖家数据，仅删除 Redis 中的数据
     */
    @DeleteMapping("/{sellerId}")
    public ResponseEntity<?> deleteSeller(@PathVariable int sellerId) {
        String redisKey = "seller:" + sellerId;
        Seller seller = sellerRedisTemplate.opsForValue().get(redisKey);
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Seller not found");
        }
        sellerRedisTemplate.delete(redisKey);
        return ResponseEntity.accepted().build();
    }

    /**
     * 清理所有卖家数据（例如用于测试），清空所有以 "seller:" 开头的键
     */
    @PatchMapping("/cleanup")
    public ResponseEntity<?> cleanup() {
        logger.warn("Cleanup requested");
        try {
            // 清除 Redis 中所有卖家键（以 seller: 开头）
            sellerRedisTemplate.delete(sellerRedisTemplate.keys("seller:*"));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Seller data cleaned");
        } catch (Exception e) {
            logger.error("Error during seller cleanup: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     * 重置卖家数据（例如用于测试），根据需要可以清空并初始化数据
     */
    @PatchMapping("/reset")
    public ResponseEntity<?> reset() {
        logger.warn("Reset requested");
        try {
            // 此处可以定义如何重置卖家数据，例如删除所有数据并插入初始数据
            sellerRedisTemplate.delete(sellerRedisTemplate.keys("seller:*"));
            return ResponseEntity.ok("Seller data reset");
        } catch (Exception e) {
            logger.error("Error during seller reset: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
