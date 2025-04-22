package com.example.shipment.repository;

import com.example.common.entities.PackageStatus;
import com.example.shipment.model.Package;
import com.example.shipment.model.PackageId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class RedisPackageRepository implements IPackageRepository {

    private static final String PACKAGE_PREFIX = "package:";

    @Autowired
    private RedisTemplate<String, Package> redisTemplate;

    /**
     * 根据 Package 对象生成唯一的 Redis key，
     * 格式：package:{customerId}:{orderId}:{sellerId}:{packageId}
     */
    private String generateKey(Package pkg) {
        PackageId id = pkg.getId();
        return PACKAGE_PREFIX + id.getCustomerId() + ":" + id.getOrderId() + ":" + pkg.getSellerId() + ":" + id.getPackageId();
    }

    /**
     * 根据各字段生成查询 pattern，如果 sellerId 为 null，则匹配所有该部分
     */
    private String generatePattern(Integer customerId, Integer orderId, Integer sellerId) {
        StringBuilder sb = new StringBuilder(PACKAGE_PREFIX);
        sb.append(customerId != null ? customerId : "*").append(":")
                .append(orderId != null ? orderId : "*").append(":")
                .append(sellerId != null ? sellerId : "*").append(":*");
        return sb.toString();
    }

    /**
     * 从 Redis 中获取所有包裹数据，并进行聚合计算，
     * 返回每个卖家最早（按 customerId|orderId 字符串比较）的订单号。
     * 返回结果为 List<Object[]>，每个元素包含：
     * [sellerId, "customerId|orderId"]
     */
    @Override
    public List<Object[]> getOldestOpenShipmentPerSeller(PackageStatus status) {
        // 获取所有包裹 key
        Set<String> keys = redisTemplate.keys(PACKAGE_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<Package> allPackages = redisTemplate.opsForValue().multiGet(keys);
        if (allPackages == null) {
            return Collections.emptyList();
        }
        // 筛选状态符合的包裹
        List<Package> filtered = allPackages.stream()
                .filter(pkg -> pkg.getStatus() == status)
                .collect(Collectors.toList());
        // 按 sellerId 分组，同时组内比较 (customerId|orderId) 的最小值
        Map<Integer, String> aggregate = new HashMap<>();
        for (Package pkg : filtered) {
            int sellerId = pkg.getSellerId();
            String orderKey = pkg.getId().getCustomerId() + "|" + pkg.getId().getOrderId();
            aggregate.merge(sellerId, orderKey, (existing, current) ->
                    (current.compareTo(existing) < 0) ? current : existing);
        }
        // 将聚合结果转换为 List<Object[]>: [sellerId, orderKey]
        List<Object[]> result = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : aggregate.entrySet()) {
            result.add(new Object[]{entry.getKey(), entry.getValue()});
        }
        return result;
    }

    /**
     * 根据 customerId、orderId、sellerId 和指定状态查询包裹
     */
    @Override
    public List<Package> getShippedPackagesByOrderAndSeller(int customerId, int orderId, int sellerId, PackageStatus status) {
        String pattern = generatePattern(customerId, orderId, sellerId);
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<Package> packages = redisTemplate.opsForValue().multiGet(keys);
        if (packages == null) {
            return Collections.emptyList();
        }
        return packages.stream()
                .filter(pkg -> pkg.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * 根据 customerId 与 orderId 查询包裹中指定状态的数量
     */
    @Override
    public int getTotalDeliveredPackagesForOrder(int customerId, int orderId, PackageStatus status) {
        String pattern = generatePattern(customerId, orderId, null);
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return 0;
        }
        List<Package> packages = redisTemplate.opsForValue().multiGet(keys);
        if (packages == null) {
            return 0;
        }
        return (int) packages.stream()
                .filter(pkg -> pkg.getStatus() == status)
                .count();
    }

    /**
     * 查询指定 customerId 与 orderId 的所有包裹
     */
    @Override
    public List<Package> findAllByOrderIdAndCustomerId(int customerId, int orderId) {
        String pattern = generatePattern(customerId, orderId, null);
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<Package> packages = redisTemplate.opsForValue().multiGet(keys);
        return packages == null ? Collections.emptyList() : packages;
    }

    /**
     * 删除所有以 PACKAGE_PREFIX 开头的 Package 数据
     */
    @Override
    public void deleteAll() {
        Set<String> keys = redisTemplate.keys(PACKAGE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 保存单个 Package 到 Redis，使用过期时间 1 小时
     */
    @Override
    public void savePackage(Package pkg) {
        String key = generateKey(pkg);
        redisTemplate.opsForValue().set(key, pkg, 1, TimeUnit.HOURS);
    }

    /**
     * 批量保存 Package 到 Redis
     */
    @Override
    public void saveAll(List<Package> packages) {
        if (packages == null || packages.isEmpty()) return;
        Map<String, Package> map = new HashMap<>();
        for (Package pkg : packages) {
            map.put(generateKey(pkg), pkg);
        }
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 保存 Package，调用 savePackage 方法
     */
    @Override
    public void save(Package pack) {
        savePackage(pack);
    }
}
