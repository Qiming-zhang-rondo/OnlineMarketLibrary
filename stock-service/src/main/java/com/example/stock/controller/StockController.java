package com.example.stock.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.common.events.IncreaseStock;
import com.example.stock.model.StockItem;
import com.example.stock.model.StockItemId;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.StockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;
    private final StockRepository stockRepository;
    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    @Autowired
    public StockController(StockService stockService, StockRepository stockRepository) {
        this.stockService = stockService;
        this.stockRepository = stockRepository;
    }

    @PatchMapping("/")
    public ResponseEntity<Void> increaseStock(@RequestBody IncreaseStock increaseStock) {
        logger.info("[IncreaseStock] received for item id {}", increaseStock.getProductId());

        
        StockItem stockItem = stockRepository.findById(increaseStock.getSellerId(), increaseStock.getProductId());

        // If the inventory item does not exist, 404 is returned
        if (stockItem == null) {
            logger.info("[IncreaseStock] Item not found for product id {}", increaseStock.getProductId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //If the inventory item exists, call the service layer method to update the inventory
        stockService.increaseStock(increaseStock);
        logger.info("[IncreaseStock] completed for item id {}", increaseStock.getProductId());

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/")
    public ResponseEntity<Void> addStockItem(@RequestBody com.example.common.entities.StockItem commonStockItem) {
        logger.info("[AddStockItem] received for item id {}", commonStockItem.getProduct_id());
        try {
            StockItemId stockItemId = new StockItemId(commonStockItem.getSeller_id(), commonStockItem.getProduct_id());
            StockItem stockItem = new StockItem();
            stockItem.setId(stockItemId);
            stockItem.setUpdatedAt(LocalDateTime.now());
            stockItem.setQtyAvailable(commonStockItem.getQty_available());
            stockItem.setQtyReserved(commonStockItem.getQty_reserved());
            stockItem.setOrderCount(commonStockItem.getOrder_count());
            stockItem.setYtd(commonStockItem.getYtd());
            stockItem.setData(commonStockItem.getData());
            stockItem.setVersion(commonStockItem.getVersion());
            stockService.createStockItem(stockItem);

            logger.info("[AddStockItem] completed for item id {}", stockItem.getId().getProductId());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("[AddStockItem] failed for item id {}", commonStockItem.getProduct_id(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{sellerId}/{productId}")
    public ResponseEntity<StockItem> getBySellerIdAndProductId(@PathVariable int sellerId,
            @PathVariable int productId) {
        StockItem item = stockRepository.findById(sellerId, productId);
        if (item != null) {
            StockItem responseItem = new StockItem(
                    item.getId(),
                    item.getQtyAvailable(),
                    item.getCreatedAt());
            responseItem.setQtyReserved(item.getQtyReserved());
            responseItem.setOrderCount(item.getOrderCount());
            responseItem.setYtd(item.getYtd());
            responseItem.setData(item.getData());
            responseItem.setVersion(item.getVersion());
            responseItem.setUpdatedAt(item.getUpdatedAt());
            responseItem.setActive(item.isActive());
            return new ResponseEntity<>(responseItem, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<List<StockItem>> getBySellerId(@PathVariable int sellerId) {
        logger.info("[GetBySeller] received for seller {}", sellerId);
        if (sellerId <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<StockItem> items = stockRepository.findBySellerId(sellerId);
        if (items != null && !items.isEmpty()) {
            List<StockItem> responseItems = items.stream()
                    .map(item -> {
                        StockItem responseItem = new StockItem(
                                item.getId(),
                                item.getQtyAvailable(),
                                item.getCreatedAt());

                        responseItem.setQtyReserved(item.getQtyReserved());
                        responseItem.setOrderCount(item.getOrderCount());
                        responseItem.setYtd(item.getYtd());
                        responseItem.setData(item.getData());
                        responseItem.setVersion(item.getVersion());
                        return responseItem;
                    })
                    .collect(Collectors.toList());
            logger.info("[GetBySellerId] returning seller {} items...", sellerId);
            return new ResponseEntity<>(responseItems, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/cleanup")
    public ResponseEntity<Void> cleanup() {
        logger.warn("Cleanup requested at {}", LocalDateTime.now());
        stockService.cleanup();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/reset")
    public ResponseEntity<Void> reset() {
        logger.warn("Reset requested at {}", LocalDateTime.now());
        stockService.reset();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
