package com.example.seller.repository;

import com.example.common.entities.OrderStatus;
import com.example.seller.model.OrderEntry;
import com.example.seller.model.OrderEntryId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class RedisOrderEntryRepository implements IOrderEntryRepository {

    private static final String ORDER_ENTRY_PREFIX = "orderEntry:";

    @Autowired
    private RedisTemplate<String, OrderEntry> orderEntryRedisTemplate;

    /**
     * 生成 Redis key，例如 orderEntry:{customerId}:{orderId}:{sellerId}:{productId}
     */
    private String generateKey(OrderEntryId id) {
        return ORDER_ENTRY_PREFIX
                + id.getCustomerId() + ":"
                + id.getOrderId() + ":"
                + id.getSellerId() + ":"
                + id.getProductId();
    }

    @Override
    public Optional<OrderEntry> findById(OrderEntryId id) {
        String key = generateKey(id);
        OrderEntry orderEntry = orderEntryRedisTemplate.opsForValue().get(key);
        return Optional.ofNullable(orderEntry);
    }

    @Override
    public List<OrderEntry> findByCustomerIdAndOrderId(int customerId, int orderId) {
        String pattern = ORDER_ENTRY_PREFIX + customerId + ":" + orderId + ":*";
        Set<String> keys = orderEntryRedisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<OrderEntry> entries = orderEntryRedisTemplate.opsForValue().multiGet(keys);
        return entries == null ? Collections.emptyList() : entries;
    }

    @Override
    public List<OrderEntry> findAllBySellerId(int sellerId) {
        // 假设 key 格式为 orderEntry:*:*:{sellerId}:*
        String pattern = ORDER_ENTRY_PREFIX + "*:*:" + sellerId + ":*";
        Set<String> keys = orderEntryRedisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<OrderEntry> entries = orderEntryRedisTemplate.opsForValue().multiGet(keys);
        return entries == null ? Collections.emptyList() : entries;
    }

    @Override
    public List<Object[]> findAllSellerAggregates(List<OrderStatus> statuses) {
        // Redis 不支持复杂的聚合查询，这里加载所有 OrderEntry 数据后，在 Java 层过滤聚合。
        Set<String> keys = orderEntryRedisTemplate.keys(ORDER_ENTRY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<OrderEntry> allEntries = orderEntryRedisTemplate.opsForValue().multiGet(keys);
        if (allEntries == null) {
            return Collections.emptyList();
        }
        // 过滤出指定状态的数据
        List<OrderEntry> filtered = allEntries.stream()
                .filter(entry -> statuses.contains(entry.getOrderStatus()))
                .collect(Collectors.toList());
        // 按 sellerId 聚合，计算：
        // countOrders（唯一 orderId 数量）、countItems、totalAmount、totalFreight、totalInvoice
        Map<Integer, Aggregates> aggMap = new HashMap<>();
        for (OrderEntry entry : filtered) {
            int sellerId = entry.getSellerId();
            Aggregates agg = aggMap.getOrDefault(sellerId, new Aggregates());
            agg.orderIds.add(entry.getOrderId());
            agg.countItems++;
            agg.totalAmount += entry.getTotalAmount();
            agg.totalFreight += entry.getFreightValue();
            agg.totalInvoice += entry.getTotalInvoice();
            aggMap.put(sellerId, agg);
        }
        // 将聚合结果封装成 Object[] 数组： [sellerId, countOrders, countItems, totalAmount, totalFreight, totalInvoice]
        List<Object[]> result = new ArrayList<>();
        for (Map.Entry<Integer, Aggregates> e : aggMap.entrySet()) {
            Aggregates agg = e.getValue();
            result.add(new Object[]{
                    e.getKey(),
                    (long) agg.orderIds.size(),
                    (long) agg.countItems,
                    agg.totalAmount,
                    agg.totalFreight,
                    agg.totalInvoice
            });
        }
        return result;
    }

    // 辅助类：存储聚合信息
    private static class Aggregates {
        Set<Integer> orderIds = new HashSet<>();
        int countItems = 0;
        double totalAmount = 0;
        double totalFreight = 0;
        double totalInvoice = 0;
    }

    @Override
    public void saveOrderEntry(OrderEntry orderEntry) {
        save(orderEntry);
    }

    @Override
    public void deleteOrderEntry(OrderEntry orderEntry) {
        String key = generateKey(orderEntry.getId());
        orderEntryRedisTemplate.delete(key);
    }

    @Override
    public void deleteAll() {
        Set<String> keys = orderEntryRedisTemplate.keys(ORDER_ENTRY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            orderEntryRedisTemplate.delete(keys);
        }
    }

    @Override
    public void saveAll(List<OrderEntry> orderEntries) {
        if (orderEntries == null || orderEntries.isEmpty()) {
            return;
        }
        Map<String, OrderEntry> map = new HashMap<>();
        for (OrderEntry entry : orderEntries) {
            map.put(generateKey(entry.getId()), entry);
        }
        orderEntryRedisTemplate.opsForValue().multiSet(map);
    }

    @Override
    public void save(OrderEntry orderEntry) {
        String key = generateKey(orderEntry.getId());
        orderEntryRedisTemplate.opsForValue().set(key, orderEntry, 30, TimeUnit.MINUTES);
    }
}
