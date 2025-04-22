package com.example.seller.service;

import com.example.seller.service.IMaterializedViewServiceCache;
import com.example.seller.model.OrderSellerView;
import com.example.seller.repository.IOrderEntryRepository;
import com.example.seller.service.IMaterializedViewService;
import com.example.common.events.InvoiceIssued;
import com.example.common.events.ShipmentNotification;
import com.example.common.entities.OrderItem;
import com.example.common.entities.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MaterializedViewServiceCore implements IMaterializedViewService {

    private static final Logger logger = LoggerFactory.getLogger(MaterializedViewServiceCore.class);
    private final IOrderEntryRepository orderEntryRepository;
    private final IMaterializedViewServiceCache orderSellerViewCache;

    public MaterializedViewServiceCore(IOrderEntryRepository orderEntryRepository,
                                       IMaterializedViewServiceCache orderSellerViewCache) {
        this.orderEntryRepository = orderEntryRepository;
        this.orderSellerViewCache = orderSellerViewCache;
    }

    @Override
    public void initializeMaterializedView() {
        // 先清除已有缓存
        orderSellerViewCache.clear();

        // 定义需要聚合的订单状态
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

            logger.info("Initializing cache for sellerId {}: {}", sellerId, view);
            orderSellerViewCache.updateSellerView(sellerId, view);
        }
    }

    @Override
    public OrderSellerView getSellerView(int sellerId) {
        return orderSellerViewCache.getSellerView(sellerId);
    }

    @Override
    public void processInvoiceIssued(InvoiceIssued invoiceIssued) {
        logger.info("Processing InvoiceIssued event: {}", invoiceIssued);
        if (invoiceIssued.getItems() == null) {
            logger.error("InvoiceIssued items are null. Event: {}", invoiceIssued);
            return;
        }
        invoiceIssued.getItems().stream()
                .collect(Collectors.groupingBy(OrderItem::getSellerId))
                .forEach((sellerId, itemsForSeller) -> {
                    OrderSellerView view = orderSellerViewCache.getSellerView(sellerId);
                    if (view == null) {
                        view = new OrderSellerView();
                        view.setSellerId(sellerId);
                    }
                    view.setCountOrders(view.getCountOrders() + 1);
                    view.setCountItems(view.getCountItems() + calculateTotalItems(itemsForSeller));
                    view.setTotalAmount(view.getTotalAmount() + calculateTotalAmount(itemsForSeller));
                    view.setTotalFreight(view.getTotalFreight() + calculateTotalFreight(itemsForSeller));
                    // 假设 InvoiceIssued 中有总发票金额字段
                    view.setTotalInvoice(view.getTotalInvoice() + invoiceIssued.getTotalInvoice());
                    orderSellerViewCache.updateSellerView(sellerId, view);
                    logger.info("Updated cache for sellerId {}: {}", sellerId, view);
                });
    }

    @Override
    public void processShipmentNotification(ShipmentNotification notification) {
        logger.info("Processing ShipmentNotification event: {}", notification);
        List<Integer> sellerIds = orderEntryRepository.findByCustomerIdAndOrderId(
                        notification.getCustomerId(), notification.getOrderId()).stream()
                .map(orderEntry -> orderEntry.getId().getSellerId())
                .distinct()
                .toList();
        for (Integer sellerId : sellerIds) {
            OrderSellerView view = orderSellerViewCache.getSellerView(sellerId);
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
            orderSellerViewCache.updateSellerView(sellerId, view);
            logger.info("Updated cache for sellerId {}: {}", sellerId, view);
        }
    }


    // 辅助计算方法
    private int calculateTotalItems(List<OrderItem> items) {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    private float calculateTotalAmount(List<OrderItem> items) {
        return (float) items.stream().mapToDouble(item -> item.getUnitPrice() * item.getQuantity()).sum();
    }

    private float calculateTotalFreight(List<OrderItem> items) {
        return (float) items.stream().mapToDouble(OrderItem::getFreightValue).sum();
    }
}
