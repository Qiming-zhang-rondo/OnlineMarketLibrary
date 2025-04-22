package com.example.seller.repository;

import com.example.seller.dto.SellerDashboard;
import com.example.seller.model.OrderSellerView;
import com.example.seller.service.IMaterializedViewServiceCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisOrderSellerViewCache implements IMaterializedViewServiceCache {

    private static final String DASHBOARD_KEY = "sellerDashboard:";

    @Autowired
    private RedisTemplate<String, SellerDashboard> redisTemplate;

    @Override
    public void clear() {
        Set<String> keys = redisTemplate.keys(DASHBOARD_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

//    @Override
//    public void updateSellerView(int sellerId, SellerDashboard dashboard) {
//        String key = DASHBOARD_KEY + sellerId;
//        redisTemplate.opsForValue().set(key, dashboard, 1, TimeUnit.HOURS);
//    }

    @Override
    public OrderSellerView getSellerView(int sellerId) {
        String key = DASHBOARD_KEY + sellerId;
        return redisTemplate.opsForValue().get(key).getSellerView();
    }

    @Override
    public void updateSellerView(int sellerId, OrderSellerView view) {
        // 构造 Redis key，比如 "sellerDashboard:123"
        String key = DASHBOARD_KEY + sellerId;
        // 根据业务逻辑将 OrderSellerView 封装到 SellerDashboard 中
        SellerDashboard dashboard = new SellerDashboard();
        dashboard.setSellerView(view);
        // 如果有订单详情数据，也可以设置; 此处简单只包装了 view
        redisTemplate.opsForValue().set(key, dashboard, 1, TimeUnit.HOURS);
//        // 将 view 对象存入 Redis，同时设置 1 小时有效期
//        redisTemplate.opsForValue().set(key, view, 1, TimeUnit.HOURS);
    }
}
