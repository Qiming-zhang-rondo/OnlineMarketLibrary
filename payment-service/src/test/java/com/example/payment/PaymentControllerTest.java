// package com.example.payment;

// import com.example.common.entities.PaymentType;
// import com.example.common.events.InvoiceIssued;
// import com.example.common.requests.CustomerCheckout;
// import com.example.payment.controller.PaymentController;
// import com.example.payment.model.OrderPayment;
// import com.example.payment.model.OrderPaymentCard;
// import com.example.payment.model.OrderPaymentCardId;
// import com.example.payment.model.OrderPaymentId;
// import com.example.payment.repository.PaymentRepository;
// import com.example.payment.service.PaymentService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import java.time.LocalDateTime;
// import java.time.Month;
// import java.util.Collections;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.mockito.ArgumentMatchers.intThat;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// public class PaymentControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private PaymentService paymentService;

//     @Autowired
//     private PaymentRepository paymentRepository;

//     private InvoiceIssued invoiceIssued;

//     @BeforeEach
//     public void setup() {
//         // 清空数据库中的数据
//         paymentRepository.deleteAll();

//         // 初始化 CustomerCheckout 对象
//         CustomerCheckout customerCheckout = new CustomerCheckout();
//         customerCheckout.setCustomerId(123);
//         customerCheckout.setPaymentType("CREDIT_CARD");
//         customerCheckout.setInstallments(3);

//         // 初始化 InvoiceIssued 对象
//         invoiceIssued = new InvoiceIssued();
//         invoiceIssued.setCustomer(customerCheckout);
//         invoiceIssued.setOrderId(1);
//         invoiceIssued.setTotalInvoice(100.0f);

//         // 创建并保存 OrderPayment 对象
//         OrderPayment orderPayment = new OrderPayment();
//         OrderPaymentId orderPaymentId = new OrderPaymentId(invoiceIssued.getCustomer().getCustomerId(),
//                 invoiceIssued.getOrderId(), 100);
//         orderPayment.setId(orderPaymentId);
//         orderPayment.setCreatedAt(LocalDateTime.now());
//         orderPayment.setInstallments(3);
//         orderPayment.setType(PaymentType.CREDIT_CARD);

//         OrderPaymentCard paymentCard = new OrderPaymentCard();
//         OrderPaymentCardId orderPaymentCardId = new OrderPaymentCardId(invoiceIssued.getCustomer().getCustomerId(),
//                 invoiceIssued.getOrderId(), 100);
//         paymentCard.setId(orderPaymentCardId);
//         paymentCard.setCardNumber("1234567812345678");
//         paymentCard.setCardHolderName("John Doe");
//         paymentCard.setCardExpiration(LocalDateTime.of(2030, Month.DECEMBER, 31, 23, 59));
//         paymentCard.setCardBrand("MasterCard");
//         paymentCard.setOrderPayment(orderPayment);

//         paymentRepository.save(orderPayment);
//     }

//     @AfterEach
//     public void cleanup() {
//         // 清空数据库中的数据
//         paymentRepository.deleteAll();
//     }

//     @Test
//     public void testProcessPaymentManually() throws Exception {
//         // 设置测试的 JSON 请求内容
//         String jsonRequest = "{ " +
//                 "\"customer\": { " +
//                 "\"customerId\": 123, " +
//                 "\"firstName\": \"John\", " +
//                 "\"lastName\": \"Doe\", " +
//                 "\"street\": \"123 Main St\", " +
//                 "\"complement\": \"Apt 4B\", " +
//                 "\"city\": \"New York\", " +
//                 "\"state\": \"NY\", " +
//                 "\"zipCode\": \"10001\", " +
//                 "\"paymentType\": \"CREDIT_CARD\", " +
//                 "\"cardNumber\": \"1234567890123456\", " +
//                 "\"cardHolderName\": \"John Doe\", " +
//                 "\"cardExpiration\": \"1230\", " +
//                 "\"cardSecurityNumber\": \"123\", " +
//                 "\"cardBrand\": \"VISA\", " +
//                 "\"installments\": 3, " +
//                 "\"instanceId\": \"instance-123\" " +
//                 "}, " +
//                 "\"orderId\": 1, " +
//                 "\"invoiceNumber\": \"INV-1001\", " +
//                 "\"issueDate\": \"2024-11-01T12:00:00\", " +
//                 "\"totalInvoice\": 100.0, " +
//                 "\"items\": [], " + // 空项数组，示例中未涉及具体项数据
//                 "\"instanceId\": \"instance-123\" " +
//                 "}";

//         // 执行 POST 请求来处理支付
//         mockMvc.perform(post("/payment/processPayment")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(jsonRequest))
//                 .andExpect(status().isOk());

//         // 验证数据库中是否存在处理后的记录
//         List<OrderPayment> payments = paymentRepository.findAllByCustomerIdAndOrderId(123, 1);

//         // 验证记录是否存在
//         assertFalse(payments.isEmpty(), "支付记录应该存在");

//         // 验证支付记录的详细字段值
//         OrderPayment payment = payments.get(0);
//         assertEquals(123, payment.getId().getCustomerId(), "客户ID应为123");
//         assertEquals(1, payment.getId().getOrderId(), "订单ID应为1");
//         assertEquals(3, payment.getInstallments(), "分期数应为3");
//         assertEquals(100.0f, payment.getValue(), 0.01, "支付总额应为100.0");
//         assertEquals("CREDIT_CARD", payment.getType().toString(), "支付类型应为CREDIT_CARD");
//         assertEquals("SUCCEEDED", payment.getStatus().toString(), "支付状态应为SUCCESSED");
//         assertNotNull(payment.getCreatedAt(), "创建时间不应为空");

//         // 验证关联的 OrderPaymentCard 记录
//         assertNotNull(payment.getOrderPaymentCard(), "应存在关联的 OrderPaymentCard");
//         assertEquals("1234567890123456", payment.getOrderPaymentCard().getCardNumber(), "卡号应为1234567890123456");
//         assertEquals("John Doe", payment.getOrderPaymentCard().getCardHolderName(), "持卡人应为John Doe");
//         LocalDateTime expectedExpirationDate = LocalDateTime.of(2030, 12, 1, 0, 0);
//         assertEquals(expectedExpirationDate, payment.getOrderPaymentCard().getCardExpiration(),
//                 "卡有效期应为2030-12-01T00:00");
//         assertEquals("VISA", payment.getOrderPaymentCard().getCardBrand(), "卡品牌应为VISA");
//     }

//     @Test
//     public void testGetPaymentByOrderId_NotFound() throws Exception {
//         // 删除测试数据确保找不到记录
//         paymentRepository.deleteAll();

//         // 执行 GET 请求并检查结果为 404 Not Found
//         mockMvc.perform(get("/payment/123/1"))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     public void testGetPaymentByOrderId_Found() throws Exception {
//         // 执行 GET 请求并检查结果为 200 OK
//         mockMvc.perform(get("/payment/123/1"))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     public void testCleanup() throws Exception {
//         // 执行 PATCH 请求来触发清理
//         mockMvc.perform(patch("/payment/cleanup"))
//                 .andExpect(status().isAccepted());

//         // 验证清理后数据库中数据是否被删除
//         assert paymentRepository.count() == 0;
//     }
// }
