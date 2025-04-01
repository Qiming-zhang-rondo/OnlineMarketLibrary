package com.example.seller.service;

import com.example.common.events.InvoiceIssued;
import com.example.common.events.ShipmentNotification;
import com.example.common.entities.OrderItem;
import com.example.common.entities.OrderStatus;
import com.example.seller.model.OrderSellerView;
import com.example.seller.repository.OrderEntryRepository;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MaterializedViewService {

    private static final Logger logger = LoggerFactory.getLogger(MaterializedViewService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OrderEntryRepository orderEntryRepository;

    @PostConstruct
    public void initializeMaterializedView() {
        clearRedis();
        List<OrderStatus> statuses = Arrays.asList(
                OrderStatus.INVOICED,
                OrderStatus.PAYMENT_PROCESSED,
                OrderStatus.READY_FOR_SHIPMENT,
                OrderStatus.IN_TRANSIT);

        List<Object[]> results = orderEntryRepository.findAllSellerAggregates(statuses);

        for (Object[] result : results) {
            Integer sellerId = (Integer) result[0];
            Long countOrders = (Long) result[1];
            Long countItems = (Long) result[2];
            Double totalAmount = (Double) result[3];
            Double totalFreight = (Double) result[4];
            Double totalInvoice = (Double) result[5];

            OrderSellerView view = new OrderSellerView();
            view.setSellerId(sellerId);
            view.setCountOrders(countOrders.intValue());
            view.setCountItems(countItems.intValue());
            view.setTotalAmount(totalAmount.floatValue());
            view.setTotalFreight(totalFreight.floatValue());
            view.setTotalInvoice(totalInvoice.floatValue());

            logger.info("Initializing Redis for sellerId {}: {}", sellerId, view);
            updateRedis(sellerId, view);
        }
    }

    public OrderSellerView getSellerView(int sellerId) {
        String redisKey = getRedisKey(sellerId);
        Map<Object, Object> viewData = redisTemplate.opsForHash().entries(redisKey);
        if (viewData.isEmpty()) {
            return null;
        }

        OrderSellerView view = new OrderSellerView();
        view.setSellerId(sellerId);
        view.setCountOrders(Integer.parseInt(viewData.getOrDefault("count_orders", "0").toString()));
        view.setCountItems(Integer.parseInt(viewData.getOrDefault("count_items", "0").toString()));
        view.setTotalAmount(Float.parseFloat(viewData.getOrDefault("total_amount", "0.0").toString()));
        view.setTotalFreight(Float.parseFloat(viewData.getOrDefault("total_freight", "0.0").toString()));
        view.setTotalInvoice(Float.parseFloat(viewData.getOrDefault("total_invoice", "0.0").toString()));
        return view;
    }

    @KafkaListener(topics = "invoice-issued-topic", groupId = "materialized-view-group")
    public void processInvoiceIssued(InvoiceIssued invoiceIssued) {
        logger.info("Processing InvoiceIssued event: {}", invoiceIssued);

        if (invoiceIssued.getItems() == null) {
            logger.error("InvoiceIssued items are null. Event: {}", invoiceIssued);
            return;
        }

        invoiceIssued.getItems().stream()
                .collect(Collectors.groupingBy(OrderItem::getSellerId))
                .forEach((sellerId, itemsForSeller) -> {
                    String redisKey = getRedisKey(sellerId);

                    OrderSellerView view = getSellerView(sellerId);
                    if (view == null) {
                        view = new OrderSellerView();
                        view.setSellerId(sellerId);
                    }

                    view.setCountOrders(view.getCountOrders() + 1);
                    view.setCountItems(view.getCountItems() + calculateTotalItems(itemsForSeller));
                    view.setTotalAmount(view.getTotalAmount() + calculateTotalAmount(itemsForSeller));
                    view.setTotalFreight(view.getTotalFreight() + calculateTotalFreight(itemsForSeller));
                    view.setTotalInvoice(view.getTotalInvoice() + invoiceIssued.getTotalInvoice());

                    updateRedis(sellerId, view);
                    logger.info("Updated Redis for sellerId {}: {}", sellerId, view);
                });
    }

    @KafkaListener(topics = "shipment-notification-topic", groupId = "materialized-view-group")
    public void processShipmentNotification(ShipmentNotification notification) {
        logger.info("Processing ShipmentNotification event: {}", notification);

        List<Integer> sellerIds = orderEntryRepository.findByCustomerIdAndOrderId(
                notification.getCustomerId(), notification.getOrderId()).stream()
                .map(orderEntry -> orderEntry.getId().getSellerId())
                .distinct()
                .toList();

        for (Integer sellerId : sellerIds) {
            String redisKey = getRedisKey(sellerId);

            OrderSellerView view = getSellerView(sellerId);
            if (view == null) {
                view = new OrderSellerView();
                view.setSellerId(sellerId);
            }

            switch (notification.getStatus()) {
                case APPROVED:
                    view.setCountOrders(view.getCountOrders() + 1);
                    break;
                case DELIVERY_IN_PROGRESS:
                    logger.info("Shipment in progress for sellerId: {}", sellerId);
                    break;
                case CONCLUDED:
                    logger.info("Shipment concluded for sellerId: {}", sellerId);
                    break;
                default:
                    logger.warn("Unknown shipment status: {}", notification.getStatus());
            }

            updateRedis(sellerId, view);
            logger.info("Updated Redis for sellerId {}: {}", sellerId, view);
        }
    }

    private void updateRedis(Integer sellerId, OrderSellerView view) {
        String redisKey = getRedisKey(sellerId);
        redisTemplate.opsForHash().put(redisKey, "count_orders", view.getCountOrders());
        redisTemplate.opsForHash().put(redisKey, "count_items", view.getCountItems());
        redisTemplate.opsForHash().put(redisKey, "total_amount", view.getTotalAmount());
        redisTemplate.opsForHash().put(redisKey, "total_freight", view.getTotalFreight());
        redisTemplate.opsForHash().put(redisKey, "total_invoice", view.getTotalInvoice());
    }

    private int calculateTotalItems(List<OrderItem> items) {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    private float calculateTotalAmount(List<OrderItem> items) {
        return (float) items.stream().mapToDouble(item -> item.getUnitPrice() * item.getQuantity()).sum();
    }

    private float calculateTotalFreight(List<OrderItem> items) {
        return (float) items.stream().mapToDouble(OrderItem::getFreightValue).sum();
    }

    private String getRedisKey(Integer sellerId) {
        return "seller:" + sellerId;
    }

    private void clearRedis() {
        logger.info("Clearing Redis keys related to materialized views.");

        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection()
                .scan(ScanOptions.scanOptions().match("seller:*").count(1000).build())) {
            cursor.forEachRemaining(key -> {
                String redisKey = new String(key);
                logger.info("Deleting Redis key: {}", redisKey);
                redisTemplate.delete(redisKey);
            });
        }

        logger.info("Redis keys cleared.");
    }
}
