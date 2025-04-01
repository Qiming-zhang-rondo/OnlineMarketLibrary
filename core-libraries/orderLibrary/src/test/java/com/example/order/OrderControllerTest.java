// package com.example.order;

// import com.example.common.entities.OrderStatus;
// import com.example.order.model.Order;
// import com.example.order.model.OrderId;
// import com.example.order.repository.OrderRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.MvcResult;

// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional  // 确保每次测试后数据库恢复到初始状态
// public class OrderControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private OrderRepository orderRepository;

//     @BeforeEach
//     public void setUp() {
//         // 清理数据库（可选，如果需要干净的测试环境）
//         orderRepository.deleteAll();
//     }

//     @Test
//     public void testGetByCustomerId() throws Exception {
//         // Step 1: 创建并设置第一个 Order 的 OrderId
//         OrderId orderId1 = new OrderId(1001, 5001);
//         Order order1 = new Order();
//         order1.setId(orderId1);
//         order1.setInvoiceNumber("INV-20231025-001");
//         order1.setStatus(OrderStatus.CREATED);
//         order1.setPurchaseDate(LocalDateTime.now());
//         order1.setCreatedAt(LocalDateTime.now());
//         order1.setUpdatedAt(LocalDateTime.now());
//         order1.setCountItems(3);
//         order1.setTotalAmount(100.0f);
//         order1.setTotalFreight(10.0f);
//         order1.setTotalIncentive(5.0f);
//         order1.setTotalInvoice(105.0f);
//         order1.setTotalItems(3);
    
//         // Step 2: 创建并设置第二个 Order 的 OrderId
//         OrderId orderId2 = new OrderId(1001, 5002);
//         Order order2 = new Order();
//         order2.setId(orderId2);
//         order2.setInvoiceNumber("INV-20231025-002");
//         order2.setStatus(OrderStatus.DELIVERED);
//         order2.setPurchaseDate(LocalDateTime.now());
//         order2.setCreatedAt(LocalDateTime.now());
//         order2.setUpdatedAt(LocalDateTime.now());
//         order2.setCountItems(2);
//         order2.setTotalAmount(100.0f);
//         order2.setTotalFreight(10.0f);
//         order2.setTotalIncentive(5.0f);
//         order2.setTotalInvoice(105.0f);
//         order2.setTotalItems(2);
    
//         // 保存到数据库
//         orderRepository.saveAll(Arrays.asList(order1, order2));
    
//         // 模拟 HTTP GET 请求并验证响应
//         MvcResult result = mockMvc.perform(get("/orders/1001")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andReturn();
    
//         String responseContent = result.getResponse().getContentAsString();
//         assertNotNull(responseContent);
//         assertTrue(responseContent.contains("CREATED"));
//         assertTrue(responseContent.contains("DELIVERED"));
    
//         // 验证数据库记录
//         List<Order> orders = orderRepository.findByCustomerId(1001);
//         assertEquals(2, orders.size());
//     }
    

//     @Test
//     public void testCleanup() throws Exception {
//         // 创建测试数据
//         // Step 1: 创建 OrderId 对象并设置 customerId 和 orderId
//         OrderId orderId = new OrderId();
//         orderId.setCustomerId(1001);  // 设置 customerId
//         orderId.setOrderId(5001);     // 设置 orderId

//         // Step 2: 创建 Order 对象，并为其嵌入的 OrderId 字段赋值
//         Order order = new Order();
//         order.setId(orderId);  // 将 orderId 对象设置到 Order 的 id 字段

//         // Step 3: 为 Order 的其他字段设置值
//         order.setInvoiceNumber("INV-20231025-001");      // 设置发票编号
//         order.setStatus(OrderStatus.CREATED);            // 设置订单状态
//         order.setPurchaseDate(LocalDateTime.now());      // 设置购买日期
//         order.setCreatedAt(LocalDateTime.now());         // 设置创建日期
//         order.setUpdatedAt(LocalDateTime.now());         // 设置更新日期
//         order.setCountItems(3);                          // 设置商品数量
//         order.setTotalAmount(100.0f);                    // 设置总金额
//         order.setTotalFreight(10.0f);                    // 设置总运费
//         order.setTotalIncentive(5.0f);                   // 设置总优惠
//         order.setTotalInvoice(105.0f);                   // 设置发票总金额
//         order.setTotalItems(3);                          // 设置商品总数

//         orderRepository.save(order);

//         // 执行清理操作
//         mockMvc.perform(patch("/orders/cleanup")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isAccepted());

//         // 确认数据库已清空
//         assertEquals(0, orderRepository.count());
//     }
// }
