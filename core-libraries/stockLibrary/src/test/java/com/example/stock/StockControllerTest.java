package com.example.stock;


import com.example.common.events.IncreaseStock;
import com.example.stock.model.StockItem;
import com.example.stock.model.StockItemId;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.StockService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 使用 SpringBootTest 加载完整的 Spring 应用上下文
@SpringBootTest
@AutoConfigureMockMvc // 自动配置 MockMvc
// @Transactional // 确保每个测试在事务中运行，并自动回滚
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockService stockService;

    // @BeforeEach
    // public void setup() {
    //     // 清理数据库或设置初始数据
    //     stockRepository.deleteAll();
    // }

    @Test
    public void testGetBySellerIdAndProductId() throws Exception {
        // 创建测试数据
        StockItemId stockItemId = new StockItemId(1, 100); // sellerId = 1, productId = 100
        StockItem stockItem = new StockItem(stockItemId, 50, LocalDateTime.now());
        stockItem.setQtyReserved(10);
        stockItem.setOrderCount(5);
        stockRepository.save(stockItem);
    
        // 执行 GET 请求，验证返回的 JSON 数据是否正确
        mockMvc.perform(get("/stock/1/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.seller_id").value(1))
                .andExpect(jsonPath("$.id.product_id").value(100))
                .andExpect(jsonPath("$.qtyAvailable").value(50))
                .andExpect(jsonPath("$.qtyReserved").value(10))
                .andExpect(jsonPath("$.orderCount").value(5));
    }
    

    @Test
    public void testIncreaseStock() throws Exception {
        // 创建测试数据
        StockItemId stockItemId = new StockItemId(3, 300);
        StockItem stockItem = new StockItem(stockItemId, 50, LocalDateTime.now());
        stockRepository.save(stockItem);
    
        // 执行 PATCH 请求，使用简化的 JSON 数据
        String increaseStockJson = "{"
            + "\"sellerId\": 3,"
            + "\"productId\": 300,"
            + "\"quantity\": 10"
            + "}";
    
        mockMvc.perform(patch("/stock/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(increaseStockJson))
                .andExpect(status().isAccepted());
    
        // 验证库存是否更新
        StockItem updatedItem = stockRepository.findById(stockItemId).orElseThrow();
        assertThat(updatedItem.getQtyAvailable()).isEqualTo(60);
    }
    



    @Test
    public void testAddStock() throws Exception {
        // 设置初始库存项
        String stockItemJson = "{"
        + "\"seller_id\": 4, \"product_id\": 400,"
        + "\"qty_available\": 60,"
        + "\"updated_at\": \"2024-10-10T12:00:00\""
        + "}";
        // stockRepository.save(stockItem);
        mockMvc.perform(post("/stock/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stockItemJson))
                .andExpect(status().isCreated());

        

        // 验证库存是否更新
        StockItemId stockItemId = new StockItemId(4, 400);
        StockItem updatedItem = stockRepository.findById(stockItemId.getSellerId(),stockItemId.getProductId());
        assertThat(updatedItem.getQtyAvailable()).isEqualTo(60);
    }
}
