//package com.example.shipment.repository;
//
//import com.example.shipment.model.Shipment;
//import com.example.shipment.model.ShipmentId;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//import java.util.Set;
//
//@Repository
//public class RedisShipmentRepository implements IShipmentRepository {
//
//    private static final String SHIPMENT_KEY_PREFIX = "shipment:";
//
//    @Autowired
//    private RedisTemplate<String, Shipment> redisTemplate;
//
//    private String buildKey(ShipmentId id) {
//        return SHIPMENT_KEY_PREFIX + id.getCustomerId() + "_" + id.getOrderId();
//    }
//
//    private String buildKey(Shipment shipment) {
//        return SHIPMENT_KEY_PREFIX + shipment.getCustomerId() + "_" + shipment.getOrderId();
//    }
//
//    @Override
//    public void deleteAll() {
//        Set<String> keys = redisTemplate.keys(SHIPMENT_KEY_PREFIX + "*");
//        if (keys != null && !keys.isEmpty()) {
//            redisTemplate.delete(keys);
//        }
//    }
//
//    @Override
//    public Optional<Shipment> findById(ShipmentId id) {
//        String key = buildKey(id);
//        Shipment shipment = redisTemplate.opsForValue().get(key);
//        return Optional.ofNullable(shipment);
//    }
//
//    @Override
//    public void save(Shipment shipment) {
//        String key = buildKey(shipment);
//        redisTemplate.opsForValue().set(key, shipment);
//    }
//
//    @Override
//    public void deleteShipment(Shipment shipment) {
//        String key = buildKey(shipment);
//        redisTemplate.delete(key);
//    }
//}
package com.example.shipment.repository;

import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentId;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public class RedisShipmentRepository implements IShipmentRepository {

    // 定义存储 Shipment 对象的 key 前缀，比如 "shipment:" 开头
    private static final String SHIPMENT_KEY_PREFIX = "shipment:";

    @Autowired
    private RedisTemplate<String, Shipment> redisTemplate;

    /**
     * 删除 Redis 中所有 Shipment 数据
     */
    @Override
    public void deleteAll() {
        // 查找所有以 SHIPMENT_KEY_PREFIX 开头的 key
        Set<String> keys = redisTemplate.keys(SHIPMENT_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 根据 ShipmentId 查找 Shipment 对象
     */
    @Override
    public Optional<Shipment> findById(ShipmentId id) {
        // 构造 key，假设 id.toString() 方法可以生成唯一标识
        String key = SHIPMENT_KEY_PREFIX + id.getCustomerId() + "_" + id.getOrderId();
//        String key = SHIPMENT_KEY_PREFIX + id.toString();;
        Shipment shipment = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(shipment);
    }

    /**
     * 保存 Shipment 到 Redis，存储时设置 key 格式为 "shipment:{id}"
     */
    @Override
    public void save(Shipment shipment) {
//        String key = SHIPMENT_KEY_PREFIX + shipment.getId().toString();
        String key = SHIPMENT_KEY_PREFIX + shipment.getCustomerId() + "_" + shipment.getOrderId();

        redisTemplate.opsForValue().set(key, shipment);
    }

    /**
     * 删除指定的 Shipment 数据
     */
    @Override
    public void deleteShipment(Shipment shipment) {
        String key = SHIPMENT_KEY_PREFIX + shipment.getCustomerId() + "_" + shipment.getOrderId();
//        String key = SHIPMENT_KEY_PREFIX + shipment.getId().toString();
        redisTemplate.delete(key);
    }
}
