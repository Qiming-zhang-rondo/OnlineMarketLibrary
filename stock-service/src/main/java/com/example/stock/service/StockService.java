package com.example.stock.service;

import com.example.common.driver.MarkStatus;
import com.example.common.driver.TransactionMark;
import com.example.common.driver.TransactionType;
import com.example.common.entities.CartItem;
import com.example.common.entities.ItemStatus;
import com.example.common.entities.OrderItem;
import com.example.common.entities.ProductStatus;
import com.example.common.events.*;
import com.example.stock.model.StockItem;
import com.example.stock.model.StockItemId;
import com.example.stock.repository.StockRepository;
import com.example.stock.config.StockConfig;
import com.example.stock.kafka.StockKafkaProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@Service
public class StockService implements IStockService {

    private final StockRepository stockRepository;
    private final StockConfig stockConfig;
    private static final Logger logger = LoggerFactory.getLogger(StockService.class);
    private final StockKafkaProducer stockKafkaProducer;

    @Autowired
    public StockService(StockRepository stockRepository, StockConfig stockConfig,
            StockKafkaProducer stockKafkaProducer) {
        this.stockRepository = stockRepository;
        this.stockConfig = stockConfig;
        this.stockKafkaProducer = stockKafkaProducer;
    }

    @Transactional
    @Override
    public void processProductUpdate(ProductUpdated productUpdated) {
        
        StockItem stockItem = stockRepository.findForUpdate(productUpdated.getSellerId(),
                productUpdated.getProductId());
        if (stockItem == null) {
            // log warnning and skip 
            logger.warn("Stock item not found for product update. Ignoring message. SellerId: {}, ProductId: {}",
                    productUpdated.getSellerId(), productUpdated.getProductId());
            return;
        }

    
        stockItem.setVersion(productUpdated.getVersion());
        stockRepository.save(stockItem); 

 // Construct the Kafka message TransactionMark
        TransactionMark transactionMark = new TransactionMark(
                productUpdated.getVersion(),
                TransactionType.UPDATE_PRODUCT,
                productUpdated.getSellerId(),
                MarkStatus.SUCCESS,
                "stock");

       
        stockKafkaProducer.sendProductUpdate(transactionMark);
        logger.info("Sending TransactionMark: {}", transactionMark);

    }

    @Transactional
    @Override
    public void reserveStock(ReserveStock checkout) {
        logger.info("Starting reserveStock for instanceId: {}", checkout.getInstanceId());

        logger.info("Found {} items in checkout: {}", checkout.getItems().size(), checkout.getItems());
        List<StockItem> items = new ArrayList<>();

       
        for (CartItem item : checkout.getItems()) {
 
            logger.info("Looking up StockItem for sellerId: {}, productId: {}", item.getSellerId(),
                    item.getProductId());
            Optional<StockItem> stockItemOpt = stockRepository
                    .findById(new StockItemId(item.getSellerId(), item.getProductId()));

            if (stockItemOpt.isPresent()) {
                StockItem stockItem = stockItemOpt.get();
                items.add(stockItem);
                logger.info("StockItem found: {}", stockItem);
            } else {
                logger.warn("StockItem not found for sellerId: {}, productId: {}", item.getSellerId(),
                        item.getProductId());
            }
        }

        if (items.isEmpty()) {
            logger.error("No items in checkout were retrieved from stock: {}", checkout);
            sendTransactionMark(checkout.getInstanceId(), checkout.getCustomerCheckout().getCustomerId(),
                    MarkStatus.ERROR);
            return;
        }

        List<ProductStatus> unavailableItems = new ArrayList<>();
        List<CartItem> cartItemsReserved = new ArrayList<>();
        List<StockItem> stockItemsReserved = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Iterate through each CartItem in checkout to find the corresponding stock item
        for (CartItem item : checkout.getItems()) {
            logger.info("Processing CartItem: {}", item);

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
                    logger.info("Stock reserved for CartItem: {}, remainingQty: {}", item, stockItem.getQtyAvailable());
                } else {
                    unavailableItems.add(new ProductStatus(item.getProductId(), ItemStatus.OUT_OF_STOCK,
                            stockItem.getQtyAvailable()));
                    logger.warn("Out of stock for CartItem: {}, availableQty: {}", item, stockItem.getQtyAvailable());
                }
            } else {
                unavailableItems.add(new ProductStatus(item.getProductId(), ItemStatus.UNAVAILABLE));
                logger.warn("No matching StockItem found for CartItem: {}", item);
            }
        }

        // If any stock is successfully retained, update the stock
        if (!stockItemsReserved.isEmpty()) {
            logger.info("Saving reserved StockItems: {}", stockItemsReserved.size());
            stockRepository.saveAll(stockItemsReserved); // Batch save the updated stock items
            stockRepository.flush(); // Make sure all updates are committed
        } else {
            logger.warn("No StockItems were reserved for instanceId: {}", checkout.getInstanceId());
        }

        

        if (!cartItemsReserved.isEmpty()) {
            StockConfirmed stockConfirmed = new StockConfirmed(
                    checkout.getTimestamp(),
                    checkout.getCustomerCheckout(),
                    cartItemsReserved,
                    checkout.getInstanceId());

            stockKafkaProducer.sendStockConfirmed(stockConfirmed);
            logger.info("StockConfirmed sent for instanceId: {}", checkout.getInstanceId());
        }

        if (!unavailableItems.isEmpty()) {
            if (stockConfig.isRaiseStockFailed()) {
                ReserveStockFailed reserveFailed = new ReserveStockFailed(
                        checkout.getTimestamp(),
                        checkout.getCustomerCheckout(),
                        unavailableItems,
                        checkout.getInstanceId());
                stockKafkaProducer.sendReserveStockFailed(reserveFailed);
                logger.info("ReserveStockFailed sent for instanceId: {}", checkout.getInstanceId());
            }

            if (cartItemsReserved.isEmpty()) {
                logger.warn("No items in checkout were reserved: {}", checkout);
                sendTransactionMark(checkout.getInstanceId(), checkout.getCustomerCheckout().getCustomerId(),
                        MarkStatus.NOT_ACCEPTED);
            }
        }

        logger.info("Finished reserveStock for instanceId: {}", checkout.getInstanceId());
    }

    private void sendTransactionMark(String instanceId, int customerId, MarkStatus status) {
        TransactionMark transactionMark = new TransactionMark(
                instanceId,
                TransactionType.CUSTOMER_SESSION,
                customerId,
                status,
                "stock");
        stockKafkaProducer.sendTransactionMark(transactionMark);
    }

    @Transactional
    @Override
    public void cancelReservation(PaymentFailed payment) {
        LocalDateTime now = LocalDateTime.now();

        for (OrderItem item : payment.getItems()) {
           // Query stock items one by one
            StockItem stockItem = stockRepository.findById(item.getSellerId(), item.getProductId());

            if (stockItem != null) {
                // Update the retained quantity of stock
                stockItem.setQtyReserved(stockItem.getQtyReserved() - item.getQuantity());
                stockItem.setUpdatedAt(now);
                stockRepository.save(stockItem); 
            }
        }
    }

    @Transactional
    public void increaseStock(IncreaseStock increaseStock) {

        StockItem stockItem = stockRepository.findById(increaseStock.getSellerId(), increaseStock.getProductId());

        if (stockItem == null) {
            logger.warn("Attempt to lock item {}, {} has not succeeded", increaseStock.getSellerId(),
                    increaseStock.getProductId());
            throw new RuntimeException("Attempt to lock item " + increaseStock.getProductId() + " has not succeeded");
        }

        stockItem.setQtyAvailable(stockItem.getQtyAvailable() + increaseStock.getQuantity());
        stockRepository.save(stockItem);

        stockRepository.flush();

        StockItem updatedStockItem = new StockItem();
        updatedStockItem.setSellerId(stockItem.getSellerId());
        updatedStockItem.setProductId(stockItem.getProductId());
        updatedStockItem.setQtyAvailable(stockItem.getQtyAvailable());
        updatedStockItem.setQtyReserved(stockItem.getQtyReserved());
        updatedStockItem.setOrderCount(stockItem.getOrderCount());
        updatedStockItem.setYtd(stockItem.getYtd());
        updatedStockItem.setData(stockItem.getData());

        // send kafka event
        stockKafkaProducer.sendStockUpdate(updatedStockItem);
        logger.info("Sent stock update event for item: {}, {}", stockItem.getSellerId(), stockItem.getProductId());

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
    @Transactional
    public void createStockItem(StockItem stockItem) {
        StockItem existingStockItem = stockRepository.findById(
                stockItem.getSellerId(),
                stockItem.getProductId());

        if (existingStockItem != null) {
            // If an stock item exists, update its properties
            existingStockItem.setQtyAvailable(stockItem.getQtyAvailable());
            existingStockItem.setQtyReserved(stockItem.getQtyReserved());
            existingStockItem.setOrderCount(stockItem.getOrderCount());
            existingStockItem.setYtd(stockItem.getYtd());
            existingStockItem.setData(stockItem.getData());
            existingStockItem.setVersion(stockItem.getVersion());
            existingStockItem.setUpdatedAt(LocalDateTime.now());
        } else {
            // if no stock item exist, create a new one
            stockItem.setCreatedAt(LocalDateTime.now());
            stockItem.setUpdatedAt(LocalDateTime.now());
            existingStockItem = stockItem;
        }

        // Use the save method to insert or update stock items
        stockRepository.save(existingStockItem);
    }

    @Override
    public void processPoisonReserveStock(ReserveStock reserveStock) {
        TransactionMark transactionMark = new TransactionMark(
                reserveStock.getInstanceId(),
                TransactionType.CUSTOMER_SESSION,
                reserveStock.getCustomerCheckout().getCustomerId(),
                MarkStatus.ABORT,
                "stock");
        stockKafkaProducer.sendPoisonReserveStock(transactionMark);
    }

    @Override
    public void processPoisonProductUpdate(ProductUpdated productUpdate) {
        TransactionMark transactionMark = new TransactionMark(
                productUpdate.getVersion(),
                TransactionType.UPDATE_PRODUCT,
                productUpdate.getSellerId(),
                MarkStatus.ABORT,
                "stock");
        stockKafkaProducer.sendPoisonProductUpdate(transactionMark);
    }

    @Override
    @Transactional
    public void confirmReservation(PaymentConfirmed payment) {
        LocalDateTime now = LocalDateTime.now();

        // Gets the combination of (sellerId, productId)
        List<StockItemId> ids = payment.getItems().stream()
                .map(item -> new StockItemId(item.getSellerId(), item.getProductId()))
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            logger.error("No items provided for reservation.");
            return;
        }

        // batch search
        List<StockItem> items = new ArrayList<>();
        int batchSize = 500; // batch size
        for (int i = 0; i < ids.size(); i += batchSize) {
            List<StockItemId> batch = ids.subList(i, Math.min(i + batchSize, ids.size()));
            items.addAll(stockRepository.findItemsByIds(batch));
        }

        if (items.isEmpty()) {
            logger.error("No stock items found for the provided seller and product IDs.");
            return;
        }

        // Store stock items into a Map for subsequent lookup
        Map<StockItemId, StockItem> stockItemsMap = items.stream()
                .collect(Collectors.toMap(StockItem::getId, item -> item));

        // Iterate over each OrderItem in the payment to find the corresponding
        // stock item
        for (OrderItem orderItem : payment.getItems()) {
            StockItemId stockItemId = new StockItemId(orderItem.getSellerId(), orderItem.getProductId());
            StockItem stockItem = stockItemsMap.get(stockItemId);

            if (stockItem != null) {
                // Update stock field
                stockItem.setQtyAvailable(stockItem.getQtyAvailable() - orderItem.getQuantity());
                stockItem.setQtyReserved(stockItem.getQtyReserved() - orderItem.getQuantity());
                stockItem.setOrderCount(stockItem.getOrderCount() + 1);
                stockItem.setUpdatedAt(now);
            } else {
                logger.warn("StockItem not found for sellerId: {}, productId: {}",
                        orderItem.getSellerId(), orderItem.getProductId());
            }
        }

        // Batch save the updated stock items
        stockRepository.saveAll(items);
        stockRepository.flush(); // Make sure changes are committed to the database immediately
    }

}
