package com.example.seller.repository;

import com.example.seller.dto.SellerDashboard;
import com.example.seller.model.OrderEntry;
import com.example.seller.model.Seller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisSellerRepository implements ISellerRepository {

    private static final String SELLER_PREFIX = "seller:";
    private static final String ORDER_ENTRY_PREFIX = "orderEntry:";  // 假设订单条目存储方式：orderEntry:{customerId}:{orderId}:{...}
    private static final String SELLER_DASHBOARD_PREFIX = "sellerDashboard:";

    @Autowired
    private RedisTemplate<String, Seller> sellerRedisTemplate;

    @Autowired
    private RedisTemplate<String, OrderEntry> orderEntryRedisTemplate;


//    @Qualifier("orderSellerViewRedisTemplate")
    @Autowired
    private RedisTemplate<String, SellerDashboard> sellerDashboardRedisTemplate;

    /**
     * 根据 customerId 和 orderId 查询订单条目集合
     * 这里假设订单条目的 key 格式为：orderEntry:{customerId}:{orderId}:{uniquePart}
     */
    @Override
    public List<OrderEntry> findByCustomerIdAndOrderId(int customerId, int orderId) {
        String pattern = ORDER_ENTRY_PREFIX + customerId + ":" + orderId + ":*";
        Set<String> keys = orderEntryRedisTemplate.keys(pattern);
        return keys == null || keys.isEmpty()
                ? null
                : orderEntryRedisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 根据订单条目的唯一 id（假设直接用 int id 存储）查询订单条目
     * key 格式：orderEntry:{id}
     */
    @Override
    public OrderEntry findById(int id) {
        return orderEntryRedisTemplate.opsForValue().get(ORDER_ENTRY_PREFIX + id);
    }

    /**
     * 从 Redis 中获取卖家仪表盘数据，key 格式：sellerDashboard:{sellerId}
     */
    @Override
    public SellerDashboard queryDashboard(int sellerId) {
        return sellerDashboardRedisTemplate.opsForValue().get(SELLER_DASHBOARD_PREFIX + sellerId);
    }

    /**
     * 删除所有卖家数据，删除所有 key 以 seller: 开头的数据
     */
    @Override
    public void deleteAllSellers() {
        Set<String> keys = sellerRedisTemplate.keys(SELLER_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            sellerRedisTemplate.delete(keys);
        }
    }

    /**
     * 删除所有订单条目数据，删除所有 key 以 orderEntry: 开头的数据
     */
    @Override
    public void deleteAllOrderEntries() {
        Set<String> keys = orderEntryRedisTemplate.keys(ORDER_ENTRY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            orderEntryRedisTemplate.delete(keys);
        }
    }

    /**
     * 删除所有卖家相关数据（包括卖家、订单条目和仪表盘数据）
     */
    @Override
    public void deleteAll() {
        deleteAllSellers();
        deleteAllOrderEntries();
        Set<String> dashKeys = sellerDashboardRedisTemplate.keys(SELLER_DASHBOARD_PREFIX + "*");
        if (dashKeys != null && !dashKeys.isEmpty()) {
            sellerDashboardRedisTemplate.delete(dashKeys);
        }
    }

    /**
     * 保存卖家信息到 Redis，使用 key 格式：seller:{sellerId}，并设置一个过期时间（例如 30 分钟）
     */
    @Override
    public void save(Seller seller) {
        String key = SELLER_PREFIX + seller.getId();
        sellerRedisTemplate.opsForValue().set(key, seller, 30, TimeUnit.MINUTES);
    }

    @Override
    public void deleteById(int sellerId) {
        String key = SELLER_PREFIX + sellerId;
        sellerRedisTemplate.delete(key);
    }

}

