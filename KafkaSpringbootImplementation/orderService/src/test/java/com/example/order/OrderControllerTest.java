package com.example.order;

import com.example.common.entities.OrderStatus;
import com.example.order.model.Order;
import com.example.order.model.OrderId;
import com.example.order.repository.RedisOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedisOrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        orderRepository.deleteAll(); // 清空 Redis 中的所有订单数据
    }

    @Test
    public void testGetByCustomerId() throws Exception {
        // 创建两个订单
        OrderId orderId1 = new OrderId(1001, 5001);
        Order order1 = new Order();
        order1.setId(orderId1);
        order1.setInvoiceNumber("INV-20231025-001");
        order1.setStatus(OrderStatus.CREATED);
        order1.setPurchaseDate(LocalDateTime.now());
        order1.setCreatedAt(LocalDateTime.now());
        order1.setUpdatedAt(LocalDateTime.now());
        order1.setCountItems(3);
        order1.setTotalAmount(100.0f);
        order1.setTotalFreight(10.0f);
        order1.setTotalIncentive(5.0f);
        order1.setTotalInvoice(105.0f);
        order1.setTotalItems(3);

        OrderId orderId2 = new OrderId(1001, 5002);
        Order order2 = new Order();
        order2.setId(orderId2);
        order2.setInvoiceNumber("INV-20231025-002");
        order2.setStatus(OrderStatus.DELIVERED);
        order2.setPurchaseDate(LocalDateTime.now());
        order2.setCreatedAt(LocalDateTime.now());
        order2.setUpdatedAt(LocalDateTime.now());
        order2.setCountItems(2);
        order2.setTotalAmount(100.0f);
        order2.setTotalFreight(10.0f);
        order2.setTotalIncentive(5.0f);
        order2.setTotalInvoice(105.0f);
        order2.setTotalItems(2);

        // 保存到 Redis
        orderRepository.saveAll(Arrays.asList(order1, order2));

        // 发送 GET 请求
        MvcResult result = mockMvc.perform(get("/orders/1001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("CREATED"));
        assertTrue(responseContent.contains("DELIVERED"));

        List<Order> orders = orderRepository.findByCustomerId(1001);
        assertEquals(2, orders.size());
    }

    @Test
    public void testCleanup() throws Exception {
        OrderId orderId = new OrderId(1001, 5001);
        Order order = new Order();
        order.setId(orderId);
        order.setInvoiceNumber("INV-20231025-001");
        order.setStatus(OrderStatus.CREATED);
        order.setPurchaseDate(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setCountItems(3);
        order.setTotalAmount(100.0f);
        order.setTotalFreight(10.0f);
        order.setTotalIncentive(5.0f);
        order.setTotalInvoice(105.0f);
        order.setTotalItems(3);

        orderRepository.save(order);

        mockMvc.perform(patch("/orders/cleanup")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        assertEquals(0, orderRepository.findByCustomerId(1001).size());
    }
}