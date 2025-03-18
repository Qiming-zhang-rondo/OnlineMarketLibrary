// package com.example.customer;
// import java.time.LocalDateTime;
// import com.example.customer.model.Customer;
// import com.example.customer.repository.CustomerRepository;
// import com.example.customer.service.CustomerService;
// import com.example.common.events.PaymentConfirmed;
// import com.example.common.events.PaymentFailed;
// import com.example.common.requests.CustomerCheckout;
// import com.example.common.events.DeliveryNotification;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.transaction.annotation.Transactional;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertEquals;

// @SpringBootTest
// // @DirtiesContext
// // @Transactional
// public class CustomerServiceTest {

//     @Autowired
//     private CustomerService customerService;

//     @Autowired
//     private CustomerRepository customerRepository;

//     @Autowired
//     private KafkaTemplate<String, Object> kafkaTemplate;  // 使用 KafkaTemplate

//     // @BeforeEach
//     // public void setup() {
//     //     // 清空数据库确保测试一致性
//     //     customerRepository.deleteAll();
//     // }
    
//     @Test
//     // @Transactional
//     public void testProcessPaymentConfirmed() throws InterruptedException {
        

//         // 创建并保存 Customer 对象到数据库
//         Customer customer = new Customer();
//         customer.setId(1);
//         customer.setFirstName("John");
//         customer.setLastName("Doe");
//         customer.setAddress("123 Main St");
//         customer.setCity("New York");
//         customer.setZipCode("10001");
//         customer.setState("NY");
//         customer.setCardNumber("123456789");
//         customer.setCardSecurityNumber("123");
//         customer.setCardExpiration("12/25");
//         customer.setCardHolderName("John Doe");
//         customer.setCardType("VISA");
//         customer.setSuccessPaymentCount(0); // 初始支付成功次数
//         customer.setFailedPaymentCount(0); // 初始支付失败次数
//         customer.setDeliveryCount(0); // 初始配送次数
//         customer.setCreatedAt(LocalDateTime.now());
//         customer.setUpdatedAt(LocalDateTime.now());
//         customerRepository.save(customer); 

//         CustomerCheckout customerCheckout = new CustomerCheckout();
//         customerCheckout.setCustomerId(customer.getId());
//         customerCheckout.setFirstName("John");
//         customerCheckout.setLastName("Doe");
//         customerCheckout.setCardNumber("123456789");  // 示例字段
//         customerCheckout.setCardHolderName("John Doe");  // 示例字段
//         customerCheckout.setCity("New York");
//         customerCheckout.setZipCode("10001");

//         // 创建 PaymentConfirmed 事件
//         PaymentConfirmed paymentConfirmed = new PaymentConfirmed();
//         paymentConfirmed.setCustomer(customerCheckout);

//         // 调用服务层方法
//         // customerService.processPaymentConfirmed(paymentConfirmed);

//         // 发送消息到 Kafka
//         kafkaTemplate.send("payment-confirmed-topic", paymentConfirmed);
//         Thread.sleep(100); 

//         // 从数据库中重新加载 Customer 对象，验证是否更新了 successPaymentCount
//         Customer updatedCustomer = customerRepository.findById(customer.getId());
//         assertEquals(1,updatedCustomer.getSuccessPaymentCount());
//     }

//     @Test
//     // @Transactional
//     public void testProcessPaymentFailed() throws InterruptedException {
        
//         Customer customer = new Customer();
//         customer.setId(2);
//         customer.setFirstName("Qiming");
//         customer.setLastName("Zhang");
//         customer.setAddress("111 Main St");
//         customer.setCity("Copenhagen");
//         customer.setZipCode("1300");
//         customer.setState("NY");
//         customer.setCardNumber("12311111");
//         customer.setCardSecurityNumber("123");
//         customer.setCardExpiration("12/25");
//         customer.setCardHolderName("John Doe");
//         customer.setCardType("VISA");
//         customer.setSuccessPaymentCount(0); // 初始支付成功次数
//         customer.setFailedPaymentCount(0); // 初始支付失败次数
//         customer.setDeliveryCount(0); // 初始配送次数
//         customer.setCreatedAt(LocalDateTime.now());
//         customer.setUpdatedAt(LocalDateTime.now());
//         customerRepository.save(customer); 

//         // 创建 CustomerCheckout 对象
//         CustomerCheckout customerCheckout = new CustomerCheckout();
//         customerCheckout.setCustomerId(customer.getId());
//         customerCheckout.setFirstName("John");
//         customerCheckout.setLastName("Doe");
//         customerCheckout.setCardNumber("123456789");  // 示例字段
//         customerCheckout.setCardHolderName("John Doe");  // 示例字段
//         customerCheckout.setCity("New York");
//         customerCheckout.setZipCode("10001");


//         // 创建 PaymentFailed 事件
//         PaymentFailed paymentFailed = new PaymentFailed();
//         paymentFailed.setCustomer(customerCheckout);

//         // 调用服务层方法
//         // customerService.processPaymentFailed(paymentFailed);

//         // 发送消息到 Kafka
//         kafkaTemplate.send("payment-failed-topic", paymentFailed);
//         Thread.sleep(100);

//         // 从数据库中重新加载 Customer 对象，验证是否更新了 failedPaymentCount
//         Customer updatedCustomer = customerRepository.findById(customer.getId());
//         assertEquals(1,updatedCustomer.getFailedPaymentCount());
//     }

//     @Test
//     // @Transactional
//     public void testProcessDeliveryNotification() throws InterruptedException {
        
//         Customer customer = new Customer();
//         customer.setId(3);
//         customer.setFirstName("Rondo");
//         customer.setLastName("Li");
//         customer.setAddress("landhelpvej 17");
//         customer.setCity("Copenhagen S");
//         customer.setZipCode("2300");
//         customer.setState("NY");
//         customer.setCardNumber("12311111");
//         customer.setCardSecurityNumber("123");
//         customer.setCardExpiration("12/25");
//         customer.setCardHolderName("John Doe");
//         customer.setCardType("VISA");
//         customer.setSuccessPaymentCount(0); // 初始支付成功次数
//         customer.setFailedPaymentCount(0); // 初始支付失败次数
//         customer.setDeliveryCount(0); // 初始配送次数
//         customer.setCreatedAt(LocalDateTime.now());
//         customer.setUpdatedAt(LocalDateTime.now());
//         customerRepository.save(customer); 

//         CustomerCheckout customerCheckout = new CustomerCheckout();
//         customerCheckout.setCustomerId(customer.getId());
//         customerCheckout.setFirstName("John");
//         customerCheckout.setLastName("Doe");
//         customerCheckout.setCity("New York");
//         customerCheckout.setZipCode("10001");


    

//         // 创建 DeliveryNotification 事件
//         DeliveryNotification deliveryNotification = new DeliveryNotification();
//         deliveryNotification.setCustomerId(customer.getId());  // 设置与 Customer 对应的 ID
//         deliveryNotification.setOrderId(1001);  // 示例订单 ID
//         deliveryNotification.setProductId(2001);  // 示例产品 ID

//         // 调用服务层方法
//         // customerService.processDeliveryNotification(deliveryNotification);

//         // 发送消息到 Kafka
//         kafkaTemplate.send("delivery-notification-topic", deliveryNotification);
//         Thread.sleep(100);

//         // 从数据库中重新加载 Customer 对象
//         Customer updatedCustomer = customerRepository.findById(customer.getId());

//         // 验证 deliveryCount 是否正确更新
//         assertEquals(1,updatedCustomer.getDeliveryCount());
//     }
// }
