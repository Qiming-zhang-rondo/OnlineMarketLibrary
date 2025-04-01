// package com.example.shipment;

// import com.example.shipment.model.Shipment;
// import com.example.shipment.model.ShipmentId;
// import com.example.shipment.model.Package;
// import com.example.shipment.model.PackageId;
// import com.example.shipment.repository.ShipmentRepository;
// import com.example.shipment.repository.PackageRepository;
// import com.example.shipment.service.ShipmentService;

// import com.example.common.events.PaymentConfirmed;
// import com.example.common.requests.CustomerCheckout;
// import com.example.common.entities.OrderItem;
// import com.example.common.entities.PackageStatus;
// import com.example.common.entities.ShipmentStatus;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.test.annotation.DirtiesContext;

// import java.time.LocalDateTime;
// import java.util.*;

// import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest
// @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
// public class ShipmentServiceTest {

//     @Autowired
//     private ShipmentService shipmentService;

//     @Autowired
//     private ShipmentRepository shipmentRepository;

//     @Autowired
//     private PackageRepository packageRepository;

//     @Autowired
//     private KafkaTemplate<String, Object> kafkaTemplate;

//     @BeforeEach
//     public void setup() {
//         shipmentRepository.deleteAll();
//         packageRepository.deleteAll();
//     }

//     @Test
//     public void testProcessShipment() throws InterruptedException {
//         // 创建一个 CustomerCheckout 对象
//         CustomerCheckout customerCheckout = new CustomerCheckout();
//         customerCheckout.setCustomerId(1); // 设置 customerId
//         customerCheckout.setFirstName("John");
//         customerCheckout.setLastName("Doe");
//         customerCheckout.setStreet("123 Main St");
//         customerCheckout.setZipCode("12345");
//         customerCheckout.setCity("New York");
//         customerCheckout.setState("NY");

//         // 创建一个 OrderItem 对象
//         OrderItem item1 = new OrderItem();
//         item1.setProductId(1001);
//         item1.setSellerId(2001);
//         item1.setProductName("Test Product 1");
//         item1.setQuantity(1);
//         item1.setFreightValue(50.0f); // 示例运费值

//         OrderItem item2 = new OrderItem();
//         item2.setProductId(1002);
//         item2.setSellerId(2002);
//         item2.setProductName("Test Product 2");
//         item2.setQuantity(2);
//         item2.setFreightValue(30.0f); // 示例运费值

//         List<OrderItem> items = new ArrayList<>();
//         items.add(item1);
//         items.add(item2);

//         // 创建一个 PaymentConfirmed 事件
//         PaymentConfirmed paymentConfirmed = new PaymentConfirmed();
//         paymentConfirmed.setOrderId(1003);
//         paymentConfirmed.setCustomer(customerCheckout);
//         paymentConfirmed.setItems(items); // 设置 items

//         // 处理发货事件
//         // shipmentService.processShipment(paymentConfirmed);
//         kafkaTemplate.send("payment-confirmed-topic", paymentConfirmed);
//         Thread.sleep(100);

//         // 验证数据库中的 Shipment 数据是否正确插入
//         Optional<Shipment> shipmentOpt = shipmentRepository.findById(new ShipmentId(1, 1003));
//         assertThat(shipmentOpt.isPresent()).isTrue();
//         Shipment shipment = shipmentOpt.get();
//         assertThat(shipment.getOrderId()).isEqualTo(1003);
//         assertThat(shipment.getCustomerId()).isEqualTo(1);
//         assertThat(shipment.getPackageCount()).isEqualTo(2); // 检查 packageCount 是否正确
//         assertThat(shipment.getTotalFreightValue()).isEqualTo(80.0f); // 50 + 30 + 30 (根据示例运费)
//         assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.APPROVED);
//     }

//     @Test
//     public void testUpdateShipment() throws Exception {
//         // 模拟创建发货订单
//         Shipment shipment = new Shipment();
//         ShipmentId shipmentId = new ShipmentId();
//         shipmentId.setCustomerId(1);
//         shipmentId.setOrderId(1001);
//         shipment.setId(shipmentId);
//         shipment.setPackageCount(2);
//         shipment.setTotalFreightValue(100.0f);
//         shipment.setRequestDate(LocalDateTime.now());
//         shipment.setStatus(ShipmentStatus.APPROVED);

//         // 添加必填的客户详细信息字段
//         shipment.setFirstName("John");
//         shipment.setLastName("Doe");
//         shipment.setStreet("123 Main St");
//         shipment.setZipCode("12345");
//         shipment.setCity("New York");
//         shipment.setState("NY");

//         // 保存Shipment对象
//         shipmentRepository.save(shipment);

//         // 创建第一个包裹并设置必填字段
//         Package package1 = new Package();
//         PackageId packageId1 = new PackageId(1, 1001, 1);
//         package1.setId(packageId1);
//         package1.setSellerId(123); // 示例sellerId
//         package1.setProductId(456); // 示例productId
//         package1.setProductName("Product A");
//         package1.setFreightValue(50.0f);
//         package1.setShippingDate(LocalDateTime.now());
//         package1.setQuantity(10);
//         package1.setStatus(PackageStatus.SHIPPED);

//         // 保存第一个包裹
//         packageRepository.save(package1);

//         // 创建第二个包裹并设置必填字段
//         Package package2 = new Package();
//         PackageId packageId2 = new PackageId(1, 1001, 2);
//         package2.setId(packageId2);
//         package2.setSellerId(124); // 示例sellerId
//         package2.setProductId(789); // 示例productId
//         package2.setProductName("Product B");
//         package2.setFreightValue(50.0f);
//         package2.setShippingDate(LocalDateTime.now());
//         package2.setQuantity(5);
//         package2.setStatus(PackageStatus.SHIPPED);

//         // 保存第二个包裹
//         packageRepository.save(package2);

//         // 调用服务层的更新方法
//         shipmentService.updateShipment("instance-1");

//         // 等待 100 毫秒，确保数据处理完成
//         Thread.sleep(100);

//         // 验证发货状态是否正确更新
//         Shipment updatedShipment = shipmentRepository.findById(new ShipmentId(1, 1001)).get();
//         assertThat(updatedShipment.getStatus()).isEqualTo(ShipmentStatus.CONCLUDED);

//         // 验证包裹状态是否正确更新
//         List<Package> updatedPackages = packageRepository.findAllByOrderIdAndCustomerId(1001, 1);
//         for (Package pkg : updatedPackages) {
//             assertThat(pkg.getStatus()).isEqualTo(PackageStatus.DELIVERED);
//         }
//     }

// }
