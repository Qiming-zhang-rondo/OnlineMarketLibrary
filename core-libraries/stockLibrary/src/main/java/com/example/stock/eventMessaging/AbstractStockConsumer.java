package com.example.stock.eventMessaging;

import com.example.common.events.ProductUpdated;
import com.example.common.events.ReserveStock;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.stock.service.IStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象消费者类，用于处理库存相关的事件，不依赖具体的消息中间件，
 * 同时引入反序列化方法。函数逻辑与原始的 handleXxx(Object event) 保持不变，
 * 只是将输入改为 String payload，由子类实现具体的 JSON 反序列化。
 */
public abstract class AbstractStockConsumer {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final IStockService stockService;

    protected AbstractStockConsumer(IStockService stockService) {
        this.stockService = stockService;
    }

    // 抽象反序列化方法，让子类实现 JSON 到事件对象的转换
    protected abstract ProductUpdated deserializeProductUpdated(String payload);
    protected abstract ReserveStock deserializeReserveStock(String payload);
    protected abstract PaymentConfirmed deserializePaymentConfirmed(String payload);
    protected abstract PaymentFailed deserializePaymentFailed(String payload);

    /**
     * 处理 ProductUpdated 事件
     */
    public void handleProductUpdate(String payload) {
        try {
            ProductUpdated event = deserializeProductUpdated(payload);
            logger.info("Handling product update event.");
            stockService.processProductUpdate(event);
        } catch (Exception e) {
            // 如果处理出错，可考虑发送 poison 消息
            ProductUpdated event = deserializeProductUpdated(payload);
            logger.error("Error processing product update: {}", e.getMessage());
            stockService.processPoisonProductUpdate(event);
        }
    }

    /**
     * 处理 ReserveStock 事件
     */
    public void handleReserveStock(String payload) {
        try {
            ReserveStock event = deserializeReserveStock(payload);
            logger.info("Handling reserve stock event.");
            stockService.reserveStock(event);
            logger.info("Reserve stock processed successfully.");
        } catch (Exception e) {
            ReserveStock event = deserializeReserveStock(payload);
            logger.warn("Failed to process reserve stock event: {}", e.getMessage());
            stockService.processPoisonReserveStock(event);
        }
    }

    /**
     * 处理 PaymentConfirmed 事件
     */
    public void handlePaymentConfirmed(String payload) {
        try {
            PaymentConfirmed event = deserializePaymentConfirmed(payload);
            logger.info("Handling payment confirmed event.");
            stockService.confirmReservation(event);
        } catch (Exception e) {
            PaymentConfirmed event = deserializePaymentConfirmed(payload);
            logger.error("Error processing payment confirmed: {}", e.getMessage());
            // 根据需要添加 poison 或其他异常处理逻辑
        }
    }

    /**
     * 处理 PaymentFailed 事件
     */
    public void handlePaymentFailed(String payload) {
        try {
            PaymentFailed event = deserializePaymentFailed(payload);
            logger.info("Handling payment failed event.");
            stockService.cancelReservation(event);
        } catch (Exception e) {
            PaymentFailed event = deserializePaymentFailed(payload);
            logger.error("Error processing payment failed: {}", e.getMessage());
        }
    }
}
