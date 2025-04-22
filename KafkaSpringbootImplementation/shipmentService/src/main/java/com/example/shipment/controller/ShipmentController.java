package com.example.shipment.controller;

import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentId;
import com.example.shipment.service.IShipmentService;
import com.example.shipment.repository.RedisShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/shipment")
public class ShipmentController {

    @Autowired
    private RedisTemplate<String, Shipment> shipmentRedisTemplate;

    @Autowired
    private IShipmentService shipmentService;

    @Autowired
    private RedisShipmentRepository shipmentRepository;

    private static final Logger logger = LoggerFactory.getLogger(ShipmentController.class);

    private static final String SHIPMENT_KEY_PREFIX = "shipment:";

    /**
     * 新增 Shipment 数据（仅使用 Redis 作为数据库）
     */
    @PostMapping("/")
    public ResponseEntity<?> addShipment(@RequestBody Shipment shipment) {
        try {
            String redisKey = SHIPMENT_KEY_PREFIX + shipment.getId().toString();

            // 检查 Redis 中是否已存在该 Shipment
            Shipment cachedShipment = shipmentRedisTemplate.opsForValue().get(redisKey);
            if (cachedShipment != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Shipment already exists with id: " + shipment.getId());
            }

            // 保存到 Redis，设置1小时过期时间（如有需要可以调整）
            shipmentRedisTemplate.opsForValue().set(redisKey, shipment, 1, TimeUnit.HOURS);
            // 使用 Redis 基于仓库保存
            shipmentRepository.save(shipment);

            logger.info("Shipment added, id: {}", shipment.getId());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            logger.error("Failed to add shipment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{customerId}/{orderId}")
    public ResponseEntity<Shipment> getShipment(@PathVariable int customerId, @PathVariable int orderId) {
        // 构造 Redis Key，确保与保存时保持一致
        String redisKey = SHIPMENT_KEY_PREFIX + customerId + "_" + orderId;
        Shipment shipment = shipmentRedisTemplate.opsForValue().get(redisKey);
        if (shipment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(shipment);
    }


    @PatchMapping("/{instanceId}")
    public ResponseEntity<Void> updateShipment(@PathVariable("instanceId") String instanceId) {
        try {
            // 调用Service做更新逻辑
            shipmentService.updateShipment(instanceId);
            return ResponseEntity.accepted().build(); // 单测里期待 202
        } catch (Exception e) {
            logger.error("Failed to update shipment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * 删除指定 Shipment 数据
     */
    @DeleteMapping("/{shipmentId}")
    public ResponseEntity<?> deleteShipment(@PathVariable String shipmentId) {
        try {
            String redisKey = SHIPMENT_KEY_PREFIX + shipmentId;
            shipmentRedisTemplate.delete(redisKey);
            // 如有需要，调用仓库层删除对应数据
            // shipmentRepository.deleteShipment(对应Shipment对象);
            logger.info("Shipment deleted, id: {}", shipmentId);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            logger.error("Failed to delete shipment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 清理所有 Shipment 数据（例如用于测试或重置数据）
     */
    @PatchMapping("/cleanup")
    public ResponseEntity<?> cleanup() {
        try {
            shipmentRepository.deleteAll();
            logger.info("All shipments cleaned up");
            return ResponseEntity.ok("Shipments cleared");
        } catch (Exception e) {
            logger.error("Failed to clean up shipments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 重置 Shipment 数据（根据业务需求实现）
     */
    @PatchMapping("/reset")
    public ResponseEntity<?> reset() {
        try {
            // 如果有重置逻辑，可调用 shipmentRepository.reset() 或其他方法
            logger.info("Shipments reset requested");
            return ResponseEntity.ok("Shipments reset");
        } catch (Exception e) {
            logger.error("Failed to reset shipments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 异步更新 Shipment 数据，如果需要将数据同步到其他存储，可使用 @Async 异步方法
     */
    @Async
    public void asyncUpdateShipment(Shipment shipment) {
        try {
            shipmentRepository.save(shipment);
            logger.info("Async shipment update saved for id: {}", shipment.getId());
        } catch (Exception e) {
            logger.error("Failed to update shipment asynchronously for id: {}", shipment.getId(), e);
        }
    }
}
