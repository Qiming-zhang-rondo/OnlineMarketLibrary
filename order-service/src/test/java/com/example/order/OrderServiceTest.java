// package com.example.order;
// import com.example.common.entities.CartItem;
// import com.example.common.entities.OrderStatus;
// import com.example.common.entities.ShipmentStatus;
// import com.example.common.events.PaymentConfirmed;
// import com.example.common.events.PaymentFailed;
// import com.example.common.events.ShipmentNotification;
// import com.example.common.events.StockConfirmed;
// import com.example.common.requests.CustomerCheckout;
// import com.example.order.model.Order;
// import com.example.order.model.OrderId;
// import com.example.order.repository.CustomerOrderRepository;
// import com.example.order.repository.OrderHistoryRepository;
// import com.example.order.repository.OrderRepository;
// import com.example.order.service.OrderService;



// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.test.context.ActiveProfiles;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.TimeUnit;
// import java.time.LocalDateTime;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// @ActiveProfiles("test")
// @DirtiesContext
// public class OrderServiceTest {

//     @Autowired
//     private KafkaTemplate<String, Object> kafkaTemplate;

//     @Autowired
//     private OrderRepository orderRepository;
//     @Autowired
//     private OrderHistoryRepository orderHOrderRepository;
//     @Autowired
//     private CustomerOrderRepository customerOrderRepository;

//     @Autowired
//     private OrderService orderService;

//     @BeforeEach
//     public void setup() {
//         orderRepository.deleteAll();
//         orderHOrderRepository.deleteAll();
//         customerOrderRepository.deleteAll();;
//     }

//     @Test
// public void testProcessStockConfirmed() throws Exception {
//     // Step 1: 创建 OrderId 和 Order 记录
//     OrderId orderId = new OrderId();
//     orderId.setCustomerId(1001);
//     orderId.setOrderId(1);

//     Order order = new Order();
//     order.setId(orderId);
//     order.setStatus(OrderStatus.CREATED);
//     order.setInvoiceNumber("INV-20231025-001");
//     order.setPurchaseDate(LocalDateTime.now());
//     order.setCreatedAt(LocalDateTime.now());
//     order.setUpdatedAt(LocalDateTime.now());
//     order.setCountItems(2);
//     order.setTotalAmount(100.0f);
//     order.setTotalFreight(10.0f);
//     order.setTotalIncentive(5.0f);
//     order.setTotalInvoice(105.0f);
//     order.setTotalItems(2);

//     // 保存订单到数据库
//     orderRepository.save(order);

//     // Step 2: 创建并发送 StockConfirmed 事件
//     CustomerCheckout customerCheckout = new CustomerCheckout();
//     customerCheckout.setCustomerId(1001);

//     StockConfirmed stockConfirmed = new StockConfirmed();
//     stockConfirmed.setCustomerCheckout(customerCheckout);
//     stockConfirmed.setInstanceId("test-instance");
//     List<CartItem> items = new ArrayList<>();
//     CartItem item1 = new CartItem();
//     item1.setProductId(2001);
//     item1.setSellerId(3001);
//     item1.setQuantity(2);
//     item1.setUnitPrice(50.0f);
//     item1.setFreightValue(5.0f);
//     item1.setVoucher(10.0f);
//     items.add(item1);

//     CartItem item2 = new CartItem();
//     item2.setProductId(2002);
//     item2.setSellerId(3002);
//     item2.setQuantity(1);
//     item2.setUnitPrice(100.0f);
//     item2.setFreightValue(8.0f);
//     item2.setVoucher(0.0f);
//     items.add(item2);
//     stockConfirmed.setItems(items);

//     kafkaTemplate.send("stock-confirmed-topic", stockConfirmed);

//     // Step 3: 等待消息处理
//     TimeUnit.MILLISECONDS.sleep(200);

//     // Step 4: 从数据库中查找订单
//     Order updatedOrder = orderRepository.findByCustomerIdAndOrderId(1001, 1).orElse(null);

//     // Step 5: 验证订单状态
//     assertNotNull(updatedOrder);
//     assertEquals(OrderStatus.INVOICED, updatedOrder.getStatus());
//     assertEquals(1001, updatedOrder.getCustomerId());
// }


//     @Test
// public void testProcessPaymentConfirmed() throws Exception {
//     // 创建并保存一个符合条件的订单，确保数据库中存在待更新的订单
//     OrderId orderId = new OrderId(1001, 1);
//     Order order = new Order();
//     order.setId(orderId);
//     order.setInvoiceNumber("1001-20241030-003");
//     order.setStatus(OrderStatus.CREATED); // 初始状态
//     order.setPurchaseDate(LocalDateTime.now());
//     order.setCreatedAt(LocalDateTime.now());
//     order.setUpdatedAt(LocalDateTime.now());
//     order.setCountItems(3);
//     order.setTotalAmount(100.0f);
//     order.setTotalFreight(10.0f);
//     order.setTotalIncentive(5.0f);
//     order.setTotalInvoice(105.0f);
//     order.setTotalItems(3);

//     orderRepository.save(order);  // 保存到数据库中

//     // 创建一个模拟的 PaymentConfirmed 事件
//     CustomerCheckout customerCheckout = new CustomerCheckout();
//     customerCheckout.setCustomerId(1001);
//     PaymentConfirmed paymentConfirmed = new PaymentConfirmed();
//     paymentConfirmed.setCustomer(customerCheckout);
//     paymentConfirmed.setOrderId(1);
//     paymentConfirmed.setDate(LocalDateTime.now());

//     // 发送 Kafka 消息
//     kafkaTemplate.send("payment-confirmed-topic", paymentConfirmed);

//     // 等待处理
//     TimeUnit.MILLISECONDS.sleep(200);

//     // 验证数据库中的订单状态是否为 "PAYMENT_PROCESSED"
//     Order updatedOrder = orderRepository.findByCustomerIdAndOrderId(1001, 1).orElse(null);
//     assertNotNull(updatedOrder);
//     assertEquals(OrderStatus.PAYMENT_PROCESSED, updatedOrder.getStatus());
// }



//     @Test
//     public void testProcessPaymentFailed() throws Exception {
//         OrderId orderId = new OrderId();
//         orderId.setCustomerId(1001);
//         orderId.setOrderId(1);

//         // 创建并保存订单
//         Order order = new Order();
//         order.setId(orderId);
//         order.setInvoiceNumber("INV-20231025-001");
//         order.setStatus(OrderStatus.CREATED); // 初始状态为 CREATED
//         order.setPurchaseDate(LocalDateTime.now());
//         order.setCreatedAt(LocalDateTime.now());
//         order.setUpdatedAt(LocalDateTime.now());
//         order.setCountItems(3);
//         order.setTotalAmount(100.0f);
//         order.setTotalFreight(10.0f);
//         order.setTotalIncentive(5.0f);
//         order.setTotalInvoice(115.0f);
//         order.setTotalItems(3);

//         orderRepository.save(order);
//         CustomerCheckout customerCheckout = new CustomerCheckout();
//         customerCheckout.setCustomerId(1001);

//         PaymentFailed paymentFailed = new PaymentFailed();
//         paymentFailed.setCustomer(customerCheckout);
//         paymentFailed.setOrderId(1);

//         // 发送 Kafka 消息
//         kafkaTemplate.send("payment-failed-topic", paymentFailed);
        
//         // 等待处理
//         TimeUnit.MILLISECONDS.sleep(200);

//         // 验证数据库中的订单状态是否为 "PAYMENT_FAILED"
//         Order updateOrder = orderRepository.findByCustomerIdAndOrderId(1001, 1).orElse(null);
//         assertNotNull(updateOrder);
//         assertEquals(OrderStatus.PAYMENT_FAILED, updateOrder.getStatus());
//     }

//     @Test
//     public void testProcessShipmentNotification() throws Exception {
//         // 创建订单 ID
//         OrderId orderId = new OrderId();
//         orderId.setCustomerId(1001);
//         orderId.setOrderId(1);
    
//         // 创建并保存订单，初始状态为 READY_FOR_SHIPMENT 或其他合适的初始状态
//         Order order = new Order();
//         order.setId(orderId);
//         order.setInvoiceNumber("INV-20231025-001");
//         order.setStatus(OrderStatus.READY_FOR_SHIPMENT); // 初始状态
//         order.setPurchaseDate(LocalDateTime.now());
//         order.setCreatedAt(LocalDateTime.now());
//         order.setUpdatedAt(LocalDateTime.now());
//         order.setCountItems(3);
//         order.setTotalAmount(100.0f);
//         order.setTotalFreight(10.0f);
//         order.setTotalIncentive(5.0f);
//         order.setTotalInvoice(115.0f);
//         order.setTotalItems(3);
    
//         orderRepository.save(order);
    
//         // 创建 ShipmentNotification 事件
//         ShipmentNotification shipmentNotification = new ShipmentNotification();
//         shipmentNotification.setCustomerId(1001);
//         shipmentNotification.setOrderId(1);
//         shipmentNotification.setStatus(ShipmentStatus.CONCLUDED); // 设置为完成状态
//         shipmentNotification.setEventDate(LocalDateTime.now());
    
//         // 发送 Kafka 消息
//         kafkaTemplate.send("shipment-notification-topic", shipmentNotification);
    
//         // 等待消息处理完成
//         TimeUnit.MILLISECONDS.sleep(200);
    
//         // 验证数据库中的订单状态是否更新为 "DELIVERED"
//         Order updatedOrder = orderRepository.findByCustomerIdAndOrderId(1001, 1).orElse(null);
//         assertNotNull(updatedOrder, "Order should exist in the database");
//         assertEquals(OrderStatus.DELIVERED, updatedOrder.getStatus(), "Order status should be updated to DELIVERED");
//     }
    
// }
