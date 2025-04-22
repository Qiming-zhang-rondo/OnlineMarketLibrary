package com.example.stock.service;

import com.example.common.driver.MarkStatus;
import com.example.common.driver.TransactionMark;
import com.example.common.driver.TransactionType;
import com.example.common.entities.CartItem;
import com.example.common.entities.ItemStatus;
import com.example.common.entities.OrderItem;
import com.example.common.entities.ProductStatus;
import com.example.common.events.DeliveryNotification;
import com.example.common.events.IncreaseStock;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.common.events.ProductUpdated;
import com.example.common.events.ReserveStock;
import com.example.common.events.StockConfirmed;
import com.example.common.events.ReserveStockFailed;
import com.example.stock.model.StockItem;
import com.example.stock.model.StockItemId;
import com.example.stock.model.StockItem; // 注意区分包名
import com.example.stock.config.IStockConfig;
import com.example.common.messaging.IEventPublisher;
import com.example.stock.repository.IStockRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 核心库存业务逻辑实现，不依赖于 Spring、Kafka、JPA 具体实现，
 * 只依赖于抽象的接口。
 */
public class StockServiceCore implements IStockService {

    private final IStockRepository stockRepository;
    private final IStockConfig stockConfig;
    private final IEventPublisher eventPublisher;

    public StockServiceCore(IStockRepository stockRepository,
                            IStockConfig stockConfig,
                            IEventPublisher eventPublisher) {
        this.stockRepository = stockRepository;
        this.stockConfig = stockConfig;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void processProductUpdate(ProductUpdated productUpdated) {
        StockItem stockItem = stockRepository.findForUpdate(
                productUpdated.getSellerId(), productUpdated.getProductId());
        if (stockItem == null) {
            // 日志处理：库存项不存在则忽略该更新
            System.out.println("Warning: Stock item not found for product update. SellerId: "
                    + productUpdated.getSellerId() + ", ProductId: " + productUpdated.getProductId());
            return;
        }

        stockItem.setVersion(productUpdated.getVersion());
        stockRepository.save(stockItem);

        // 构造并发送 TransactionMark 消息
        TransactionMark transactionMark = new TransactionMark(
                productUpdated.getVersion(),
                TransactionType.UPDATE_PRODUCT,
                productUpdated.getSellerId(),
                MarkStatus.SUCCESS,
                "stock");
        eventPublisher.publishEvent("TransactionMark_UPDATE_PRODUCT", transactionMark);
    }

    @Override
    public void reserveStock(ReserveStock checkout) {
        LocalDateTime now = LocalDateTime.now();
        List<StockItem> items = new ArrayList<>();

        // 根据每个 CartItem 查找对应库存项
        for (CartItem item : checkout.getItems()) {
            Optional<StockItem> stockItemOpt = stockRepository.findById(
                    item.getSellerId(), item.getProductId());
            stockItemOpt.ifPresent(items::add);
        }

        if (items.isEmpty()) {
            System.out.println("Error: No stock items found for checkout: " + checkout);
            sendTransactionMark(checkout.getInstanceId(),
                    checkout.getCustomerCheckout().getCustomerId(), MarkStatus.ERROR);
            return;
        }

        List<ProductStatus> unavailableItems = new ArrayList<>();
        List<CartItem> cartItemsReserved = new ArrayList<>();
        List<StockItem> stockItemsReserved = new ArrayList<>();

        // 检查每个 CartItem 是否有足够库存
        for (CartItem item : checkout.getItems()) {
            Optional<StockItem> stockItemOpt = items.stream()
                    .filter(s -> s.getSellerId() == item.getSellerId() && s.getProductId() == item.getProductId())
                    .findFirst();

            if (stockItemOpt.isPresent()) {
                StockItem stockItem = stockItemOpt.get();
                if (stockItem.getQtyAvailable() >= item.getQuantity()) {
                    stockItem.setQtyReserved(stockItem.getQtyReserved() + item.getQuantity());
                    stockItem.setUpdatedAt(now);
                    stockItemsReserved.add(stockItem);
                    cartItemsReserved.add(item);
                    System.out.println("Reserved stock for CartItem: " + item + ", available: " + stockItem.getQtyAvailable());
                } else {
                    unavailableItems.add(new ProductStatus(item.getProductId(), ItemStatus.OUT_OF_STOCK, stockItem.getQtyAvailable()));
                    System.out.println("Warning: Out of stock for CartItem: " + item);
                }
            } else {
                unavailableItems.add(new ProductStatus(item.getProductId(), ItemStatus.UNAVAILABLE));
                System.out.println("Warning: No matching StockItem found for CartItem: " + item);
            }
        }

        if (!stockItemsReserved.isEmpty()) {
            stockRepository.saveAll(stockItemsReserved);
            stockRepository.flush();
        }

        if (!cartItemsReserved.isEmpty()) {
            StockConfirmed stockConfirmed = new StockConfirmed(
                    checkout.getTimestamp(),
                    checkout.getCustomerCheckout(),
                    cartItemsReserved,
                    checkout.getInstanceId());
            eventPublisher.publishEvent("stock-confirmed-topic", stockConfirmed);
        }

        if (!unavailableItems.isEmpty()) {
            if (stockConfig.isRaiseStockFailed()) {
                ReserveStockFailed reserveFailed = new ReserveStockFailed(
                        checkout.getTimestamp(),
                        checkout.getCustomerCheckout(),
                        unavailableItems,
                        checkout.getInstanceId());
                eventPublisher.publishEvent("stock-failed-topic", reserveFailed);
            }
            if (cartItemsReserved.isEmpty()) {
                sendTransactionMark(checkout.getInstanceId(),
                        checkout.getCustomerCheckout().getCustomerId(), MarkStatus.NOT_ACCEPTED);
            }
        }
    }

    private void sendTransactionMark(String instanceId, int customerId, MarkStatus status) {
        TransactionMark transactionMark = new TransactionMark(
                instanceId,
                TransactionType.CUSTOMER_SESSION,
                customerId,
                status,
                "stock");
        eventPublisher.publishEvent("TransactionMark_CUSTOMER_SESSION", transactionMark);
    }

    @Override
    public void cancelReservation(PaymentFailed payment) {
        LocalDateTime now = LocalDateTime.now();
        for (OrderItem item : payment.getItems()) {
            // 获取库存项的 Optional
            Optional<StockItem> optionalStockItem = stockRepository.findById(item.getSellerId(), item.getProductId());

            // 检查库存项是否存在
            if (optionalStockItem.isPresent()) {
                StockItem stockItem = optionalStockItem.get();
                // 计算新的预留数量（可选：确保不会变为负值）
                int newQtyReserved = stockItem.getQtyReserved() - item.getQuantity();
                if(newQtyReserved < 0) {
                    newQtyReserved = 0;
                }
                // 更新库存项
                stockItem.setQtyReserved(newQtyReserved);
                stockItem.setUpdatedAt(now);
                stockRepository.save(stockItem);
            }
        }
    }

    @Override
    public void increaseStock(IncreaseStock increaseStock) {
        StockItem stockItem = stockRepository
                .findById(increaseStock.getSellerId(), increaseStock.getProductId())
                .orElseThrow(() -> new RuntimeException("Stock item not found: " + increaseStock.getProductId()));

        stockItem.setQtyAvailable(stockItem.getQtyAvailable() + increaseStock.getQuantity());
        stockRepository.save(stockItem);
        stockRepository.flush();

        StockItem updatedStockItem = new StockItem();
        updatedStockItem.setId(new StockItemId(stockItem.getSellerId(), stockItem.getProductId()));
        updatedStockItem.setSellerId(stockItem.getSellerId());
        updatedStockItem.setProductId(stockItem.getProductId());
        updatedStockItem.setQtyAvailable(stockItem.getQtyAvailable());
        updatedStockItem.setQtyReserved(stockItem.getQtyReserved());
        updatedStockItem.setOrderCount(stockItem.getOrderCount());
        updatedStockItem.setYtd(stockItem.getYtd());
        updatedStockItem.setData(stockItem.getData());

        eventPublisher.publishEvent("stock-update-topic", stockItem);
    }

    @Override
    public void cleanup() {
        stockRepository.deleteAll();
    }

    @Override
    public void reset() {
        stockRepository.reset(stockConfig.getDefaultInventory());
    }

    @Override
    public void createStockItem(StockItem stockItem) {
        Optional<StockItem> optionalExisting = stockRepository.findById(stockItem.getSellerId(), stockItem.getProductId());
        StockItem existing;
        if (optionalExisting.isPresent()) {
            existing = optionalExisting.get();
            existing.setQtyAvailable(stockItem.getQtyAvailable());
            existing.setQtyReserved(stockItem.getQtyReserved());
            existing.setOrderCount(stockItem.getOrderCount());
            existing.setYtd(stockItem.getYtd());
            existing.setData(stockItem.getData());
            existing.setVersion(stockItem.getVersion());
            existing.setUpdatedAt(LocalDateTime.now());
        } else {
            stockItem.setCreatedAt(LocalDateTime.now());
            stockItem.setUpdatedAt(LocalDateTime.now());
            existing = stockItem;
        }
        stockRepository.save(existing);
    }

    @Override
    public void processPoisonReserveStock(ReserveStock reserveStock) {
        TransactionMark transactionMark = new TransactionMark(
                reserveStock.getInstanceId(),
                TransactionType.CUSTOMER_SESSION,
                reserveStock.getCustomerCheckout().getCustomerId(),
                MarkStatus.ABORT,
                "stock");
        eventPublisher.publishEvent("TransactionMark_CUSTOMER_SESSION", transactionMark);
    }

    @Override
    public void processPoisonProductUpdate(ProductUpdated productUpdate) {
        TransactionMark transactionMark = new TransactionMark(
                productUpdate.getVersion(),
                TransactionType.UPDATE_PRODUCT,
                productUpdate.getSellerId(),
                MarkStatus.ABORT,
                "stock");
        eventPublisher.publishEvent("TransactionMark_UPDATE_PRODUCT", transactionMark);
    }

    @Override
    public void confirmReservation(PaymentConfirmed payment) {
        LocalDateTime now = LocalDateTime.now();
        List<StockItemId> ids = payment.getItems().stream()
                .map(item -> new StockItemId(item.getSellerId(), item.getProductId()))
                .collect(Collectors.toList());
        if (ids.isEmpty()) return;
        List<StockItem> items = new ArrayList<>();
        int batchSize = 500;
        for (int i = 0; i < ids.size(); i += batchSize) {
            List<StockItemId> batch = ids.subList(i, Math.min(i + batchSize, ids.size()));
            items.addAll(stockRepository.findItemsByIds(batch));
        }
        if (items.isEmpty()) return;
        Map<StockItemId, StockItem> stockMap = items.stream()
                .collect(Collectors.toMap(StockItem::getId, item -> item));
        for (OrderItem orderItem : payment.getItems()) {
            StockItemId sid = new StockItemId(orderItem.getSellerId(), orderItem.getProductId());
            StockItem stockItem = stockMap.get(sid);
            if (stockItem != null) {
                stockItem.setQtyAvailable(stockItem.getQtyAvailable() - orderItem.getQuantity());
                stockItem.setQtyReserved(stockItem.getQtyReserved() - orderItem.getQuantity());
                stockItem.setOrderCount(stockItem.getOrderCount() + 1);
                stockItem.setUpdatedAt(now);
            }
        }
        stockRepository.saveAll(items);
        stockRepository.flush();
    }
}
