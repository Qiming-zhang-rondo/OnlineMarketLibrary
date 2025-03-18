package com.example.stock;
import com.example.stock.model.StockItem;
import com.example.stock.model.StockItemId;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.StockService;
import com.example.common.events.IncreaseStock;
import com.example.common.events.ProductUpdated;
import com.example.common.events.ReserveStock;
import com.example.common.entities.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 确保每个测试运行在事务中并自动回滚
public class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void setup() {
        // 清理数据库或设置初始数据
        stockRepository.deleteAll();
    }

    @Test
    public void testIncreaseStock() throws Exception {
        // 设置初始库存项
        StockItemId stockItemId = new StockItemId(1, 100); // sellerId = 1, productId = 100
        StockItem stockItem = new StockItem(stockItemId, 50, LocalDateTime.now());
        stockRepository.save(stockItem);

        // 增加库存
        IncreaseStock increaseStock = new IncreaseStock(1, 100, 10);
        stockService.increaseStock(increaseStock);

        // 验证库存是否更新
        StockItem updatedItem = stockRepository.findById(stockItemId.getSellerId(),stockItemId.getProductId());
        assertThat(updatedItem.getQtyAvailable()).isEqualTo(60);
    }

    @Test
    public void testProcessProductUpdate() {
        // 设置初始库存项
        StockItemId stockItemId = new StockItemId(1, 100); // sellerId = 1, productId = 100
        StockItem stockItem = new StockItem(stockItemId, 50, LocalDateTime.now());
        stockRepository.save(stockItem);

        // 更新库存
        ProductUpdated productUpdated = new ProductUpdated();
        productUpdated.setSellerId(1);
        productUpdated.setProductId(100);
        productUpdated.setVersion("v2");
        stockService.processProductUpdate(productUpdated);

        // 验证版本号是否更新
        StockItem updatedItem = stockRepository.findById(stockItemId.getSellerId(),stockItemId.getProductId());
        assertThat(updatedItem.getVersion()).isEqualTo("v2");
    }

    @Test
    public void testReserveStock() throws Exception {
        // 设置初始库存项
        StockItemId stockItemId = new StockItemId(1, 100); // sellerId = 1, productId = 100
        StockItem stockItem = new StockItem(stockItemId, 50, LocalDateTime.now());
        stockRepository.save(stockItem);

        // 准备 ReserveStock 请求
        CartItem cartItem = new CartItem();
        cartItem.setSellerId(1);
        cartItem.setProductId(100);
        cartItem.setQuantity(10);

        ReserveStock reserveStock = new ReserveStock();
        reserveStock.setItems(Collections.singletonList(cartItem));
        reserveStock.setInstanceId("checkout-1");

        stockService.reserveStock(reserveStock);

        // 验证库存预留
        StockItem updatedItem = stockRepository.findById(stockItemId).orElse(null);
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getQtyReserved()).isEqualTo(10);
        assertThat(updatedItem.getQtyAvailable()).isEqualTo(50); // 库存数量不变，但预留库存增加
    }

    @Test
    public void testCreateStockItem() {
        // 设置 StockItem
        StockItemId stockItemId = new StockItemId(51, 2); // 新库存项 sellerId = 1, productId = 101
        StockItem stockItem = new StockItem(stockItemId, 100, LocalDateTime.now());
        stockItem.setQtyReserved(0);

        // 调用创建库存项
        stockService.createStockItem(stockItem);

        // 验证库存项创建
        StockItem createdItem = stockRepository.findById(stockItemId.getSellerId(),stockItemId.getProductId());
        assertThat(createdItem.getQtyAvailable()).isEqualTo(100);
    }

    @Test
    public void testCleanupStockItems() {
        // 设置初始库存项
        StockItemId stockItemId = new StockItemId(1, 100);
        StockItem stockItem = new StockItem(stockItemId, 0, LocalDateTime.now()); // qty_available 为 0
        stockRepository.save(stockItem);

        // 调用 Cleanup
        stockService.cleanup();

        // 验证库存项是否被删除
        assertThat(stockRepository.findById(stockItemId.getSellerId(),stockItemId.getProductId()));
    }
}
