// package com.example.payment;

// import com.example.common.entities.OrderItem;
// import com.example.common.events.InvoiceIssued;
// import com.example.common.requests.CustomerCheckout;
// import com.example.payment.model.OrderPayment;
// import com.example.payment.repository.PaymentRepository;
// import com.example.payment.service.PaymentService;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest // 使用 @SpringBootTest 来加载整个上下文，包括数据库连接
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 保证实际数据库使用
// @ActiveProfiles("test") // 使用 test 配置文件
// @Transactional // 确保测试中的每个操作都在事务中运行，测试完成后自动回滚
// public class PaymentServiceTest {

//     @Autowired
//     private PaymentService paymentService;

//     @Autowired
//     private PaymentRepository paymentRepository;

//     private InvoiceIssued invoiceIssued;

//     @BeforeEach
//     public void setup() {
//         // 初始化测试数据
//         CustomerCheckout customer = new CustomerCheckout();
//         customer.setCustomerId(12345);
//         customer.setFirstName("John");
//         customer.setLastName("Doe");
//         customer.setStreet("123 Main St");
//         customer.setCity("New York");
//         customer.setState("NY");
//         customer.setZipCode("10001");
//         customer.setCardNumber("4111111111111111");
//         customer.setCardHolderName("John Doe");
//         customer.setCardExpiration("1225"); // 卡片有效期，如 MMYY 格式
//         customer.setCardSecurityNumber("123"); // 卡片安全码
//         customer.setCardBrand("VISA");
//         customer.setPaymentType("CREDIT_CARD");
//         customer.setInstallments(1);
//         customer.setInstanceId("instance-001");
//         String invoiceNumber = "INV-001";
//         LocalDateTime issueDate = LocalDateTime.now();
//         float totalInvoice = 150.75f;
//         String instanceId = "instance-001";

//         // 创建订单项列表（假设至少一个订单项）
//         List<OrderItem> items = new ArrayList<>();
//         OrderItem item = new OrderItem();
//         item.setProductId(101);
//         item.setProductName("Example Product");
//         item.setQuantity(2);
//         item.setUnitPrice(50.25f);
//         item.setFreightValue(10.25f);
//         items.add(item);

//         invoiceIssued = new InvoiceIssued(customer, 123, invoiceNumber, issueDate, totalInvoice, items,
//                 instanceId);

//     }

//     @Test
//     public void testProcessPayment() {
//         // 执行支付处理
//         paymentService.processPayment(invoiceIssued);

//         // 验证数据库中的支付记录
//         List<OrderPayment> payments = paymentRepository.findAllByCustomerIdAndOrderId(
//                 invoiceIssued.getCustomer().getCustomerId(),
//                 invoiceIssued.getOrderId());

//         assertFalse(payments.isEmpty());
//         assertEquals(1, payments.size()); // 确保只有一个支付记录
//         assertEquals(invoiceIssued.getTotalInvoice(), payments.get(0).getValue());
//         OrderPayment payment = payments.get(0);
//         assertNotNull(payment.getCreatedAt());
//         assertTrue(payment.getCreatedAt().isBefore(LocalDateTime.now()));
//     }

//     @Test
//     public void testCleanup() {
//         // 插入一些支付记录
//         paymentService.processPayment(invoiceIssued);

//         // 清理所有支付记录
//         paymentService.cleanup();

//         // 确保所有支付记录被删除
//         List<OrderPayment> payments = paymentRepository.findAll();
//         assertTrue(payments.isEmpty());
//     }
// }
