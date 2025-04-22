package com.example.stock.repository;

import com.example.stock.model.StockItem;
import com.example.stock.model.StockItemId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class RedisStockRepository implements IStockRepository {

    private static final String STOCK_KEY_PREFIX = "stock:";

    @Autowired
    private RedisTemplate<String, StockItem> redisTemplate;

    /**
     * 根据 sellerId 和 productId 生成 Redis Key，格式：stock:{sellerId}:{productId}
     */
    private String generateKey(int sellerId, int productId) {
        return STOCK_KEY_PREFIX + sellerId + ":" + productId;
    }

    /**
     * 根据 StockItem 对象生成 Redis Key
     */
    private String generateKey(StockItem item) {
        return generateKey(item.getSellerId(), item.getProductId());
    }

    /**
     * 根据多个 StockItemId 生成 Key 模式（多个 id 逗号分隔组装）
     */
    private List<String> generateKeys(List<StockItemId> ids) {
        return ids.stream()
                .map(id -> generateKey(id.getSellerId(), id.getProductId()))
                .collect(Collectors.toList());
    }

    @Override
    public StockItem findForUpdate(int sellerId, int productId) {
        // Redis 不支持悲观锁，这里仅返回对应库存项
        String key = generateKey(sellerId, productId);
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public List<StockItem> findItemsByIds(List<StockItemId> ids) {
        List<String> keys = generateKeys(ids);
        List<StockItem> list = redisTemplate.opsForValue().multiGet(new HashSet<>(keys));
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public Optional<StockItem> findById(StockItemId stockItemId) {
        // 直接委托给接受两个参数的版本
        return findById(stockItemId.getSellerId(), stockItemId.getProductId());
    }

    @Override
    public Optional<StockItem> findById(int sellerId, int productId) {
        String key = generateKey(sellerId, productId);
        StockItem item = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(item);
    }




    @Override
    public List<StockItem> findBySellerId(int sellerId) {
        // 模糊匹配以 "stock:{sellerId}:" 开头的 key
        String pattern = STOCK_KEY_PREFIX + sellerId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<StockItem> list = redisTemplate.opsForValue().multiGet(keys);
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public void reset(int qty) {
        // 获取所有库存项 key
        Set<String> keys = redisTemplate.keys(STOCK_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            // 对每个库存项更新：active = true, version = "0", qtyReserved = 0, qtyAvailable = qty
            // 因为 Redis 不支持批量 UPDATE，你需要遍历每个库存项并更新后保存
            List<StockItem> items = redisTemplate.opsForValue().multiGet(keys);
            if (items != null) {
                for (StockItem item : items) {
                    // 假设 StockItem 有如下 setter 方法
                    item.setActive(true);
                    item.setVersion("0");
                    item.setQtyReserved(0);
                    item.setQtyAvailable(qty);
                    // 保存更新后的库存项
                    save(item);
                }
            }
        }
    }

    @Override
    public void save(StockItem stockItem) {
        String key = generateKey(stockItem);
        // 保存库存项到 Redis，设置过期时间1小时（可以按需要调整）
        redisTemplate.opsForValue().set(key, stockItem, 1, TimeUnit.HOURS);
    }

    @Override
    public void saveAll(List<StockItem> stockItemsReserved) {
        if (stockItemsReserved == null || stockItemsReserved.isEmpty()) return;
        Map<String, StockItem> map = new HashMap<>();
        for (StockItem item : stockItemsReserved) {
            map.put(generateKey(item), item);
        }
        redisTemplate.opsForValue().multiSet(map);
    }

    @Override
    public void flush() {
        // RedisTemplate 中通常没有 flush 方法，如果需要确保写入，可以调用 redisTemplate.execute() 调用原生命令。
        // 此处留空或者根据实际情况调用 redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Override
    public void deleteAll() {
        Set<String> keys = redisTemplate.keys(STOCK_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}

