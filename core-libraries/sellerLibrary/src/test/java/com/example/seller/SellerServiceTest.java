// package com.example.seller;

// import com.example.common.entities.OrderItem;
// import com.example.common.entities.OrderStatus;
// import com.example.common.entities.PackageStatus;
// import com.example.common.entities.ShipmentStatus;
// import com.example.common.events.DeliveryNotification;
// import com.example.common.events.InvoiceIssued;
// import com.example.common.events.PaymentFailed;
// import com.example.common.events.ShipmentNotification;
// import com.example.common.requests.CustomerCheckout;
// import com.example.seller.model.OrderEntry;
// import com.example.seller.model.OrderEntryId;
// import com.example.seller.repository.OrderEntryRepository;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.kafka.core.KafkaTemplate;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.TimeUnit;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

// @SpringBootTest
// public class SellerServiceTest {

//     @Autowired
//     private KafkaTemplate<String, Object> kafkaTemplate;

//     @Autowired
//     private OrderEntryRepository orderEntryRepository;

//     @BeforeEach
//     public void setUp() {
//         orderEntryRepository.deleteAll();
//     }

//     @Test
//     public void testProcessPaymentFailed() throws InterruptedException {
//         // Step 1: Initialize CustomerCheckout and create initial OrderEntry with a
//         // non-failed status
//         CustomerCheckout customerCheckout = new CustomerCheckout();
//         customerCheckout.setCustomerId(1);

//         OrderEntryId orderEntryId = new OrderEntryId(
//                 1,
//                 1001, // Order ID
//                 1, // Seller ID
//                 2001 // Product ID
//         );

//         OrderEntry initialEntry = new OrderEntry();
//         initialEntry.setId(orderEntryId);
//         initialEntry.setOrderStatus(OrderStatus.CREATED); // Initial status
//         orderEntryRepository.save(initialEntry); // Save initial entry

//         // Step 2: Create and send PaymentFailed event
//         PaymentFailed paymentFailed = new PaymentFailed();

//         // Set the order ID
//         paymentFailed.setOrderId(1001);
//         paymentFailed.setCustomer(customerCheckout);
//         // Set the status
//         paymentFailed.setStatus("FAILED");

//         // Set the list of items
//         List<OrderItem> items = new ArrayList<>();
//         OrderItem orderItem = new OrderItem();
//         orderItem.setOrderId(1001);
//         orderItem.setOrderItemId(1);
//         orderItem.setProductId(2001);
//         orderItem.setProductName("Test Product");
//         orderItem.setSellerId(1);
//         orderItem.setUnitPrice(100.0f);
//         orderItem.setQuantity(2);
//         orderItem.setTotalAmount(200.0f); // unitPrice * quantity
//         orderItem.setFreightValue(10.0f);
//         items.add(orderItem);
//         paymentFailed.setItems(items);

//         // Set the total amount
//         paymentFailed.setTotalAmount(210.0f); // totalAmount + freight

//         // Set the instance ID
//         paymentFailed.setInstanceId("test-instance-id");

//         kafkaTemplate.send("payment-failed-topic", paymentFailed);
//         TimeUnit.MILLISECONDS.sleep(500); // Wait for the event to process

//         // Step 3: Retrieve and verify that OrderEntry status is updated to
//         // PAYMENT_FAILED
//         List<OrderEntry> entries = orderEntryRepository.findByCustomerIdAndOrderId(
//                 paymentFailed.getCustomer().getCustomerId(), paymentFailed.getOrderId());

//         assertNotNull(entries);
//         assertEquals(1, entries.size());
//         assertEquals(OrderStatus.PAYMENT_FAILED, entries.get(0).getOrderStatus());
//     }

//     @Test
//     public void testProcessDeliveryNotification() throws InterruptedException {
//         CustomerCheckout customerCheckout = new CustomerCheckout();
//         customerCheckout.setCustomerId(1);

//         OrderEntryId orderEntryId = new OrderEntryId(
//                 customerCheckout.getCustomerId(),
//                 1001, // Order ID
//                 1, // Seller ID
//                 2001 // Product ID
//         );

//         OrderEntry initialEntry = new OrderEntry();
//         initialEntry.setId(orderEntryId);
//         initialEntry.setOrderStatus(OrderStatus.INVOICED);
//         initialEntry.setDeliveryStatus(PackageStatus.READY_TO_SHIP); // Initial status
//         orderEntryRepository.save(initialEntry);

//         DeliveryNotification deliveryNotification = new DeliveryNotification();
//         deliveryNotification.setOrderId(1001);
//         deliveryNotification.setSellerId(1);
//         deliveryNotification.setCustomerId(1);
//         deliveryNotification.setProductId(2001);
//         deliveryNotification.setStatus(PackageStatus.DELIVERED);

//         kafkaTemplate.send("delivery-notification-topic", deliveryNotification);
//         TimeUnit.MILLISECONDS.sleep(500);

//         OrderEntryId entryId = new OrderEntryId(
//                 deliveryNotification.getCustomerId(),
//                 deliveryNotification.getOrderId(),
//                 deliveryNotification.getSellerId(),
//                 deliveryNotification.getProductId());

//         OrderEntry entry = orderEntryRepository.findById(entryId).orElse(null);

//         assertNotNull(entry);
//         assertEquals(PackageStatus.DELIVERED, entry.getDeliveryStatus());
//     }

//     @Test
//     public void testProcessInvoiceIssued() throws InterruptedException {
//         // Setup CustomerCheckout data
//         CustomerCheckout customerCheckout = new CustomerCheckout();
//         customerCheckout.setCustomerId(1);
//         customerCheckout.setFirstName("John");
//         customerCheckout.setLastName("Doe");
//         customerCheckout.setStreet("123 Main St");
//         customerCheckout.setCity("Sample City");
//         customerCheckout.setState("SC");
//         customerCheckout.setZipCode("12345");
//         customerCheckout.setPaymentType("CreditCard");
//         customerCheckout.setCardNumber("4111111111111111");
//         customerCheckout.setCardHolderName("John Doe");
//         customerCheckout.setCardExpiration("1225");
//         customerCheckout.setCardSecurityNumber("123");
//         customerCheckout.setCardBrand("VISA");
//         customerCheckout.setInstallments(1);

//         // Create and send InvoiceIssued event
//         InvoiceIssued invoiceIssued = new InvoiceIssued();
//         invoiceIssued.setOrderId(1001);
//         invoiceIssued.setCustomer(customerCheckout);
//         List<OrderItem> items = new ArrayList<>();
//         OrderItem orderItem = new OrderItem();
//         orderItem.setOrderId(1001);
//         orderItem.setOrderItemId(1);
//         orderItem.setProductId(2001);
//         orderItem.setProductName("Test Product");
//         orderItem.setSellerId(1);
//         orderItem.setUnitPrice(100.0f);
//         orderItem.setQuantity(2);
//         orderItem.setTotalAmount(200.0f); // unitPrice * quantity
//         orderItem.setFreightValue(10.0f);
//         items.add(orderItem);
//         invoiceIssued.setItems(items);

//         OrderEntryId orderEntryId = new OrderEntryId(
//                 customerCheckout.getCustomerId(),
//                 1001, // Order ID
//                 1, // Seller ID
//                 2001 // Product ID
//         );

//         OrderEntry initialEntry = new OrderEntry();
//         initialEntry.setId(orderEntryId);
//         initialEntry.setOrderStatus(OrderStatus.INVOICED); // Initial status
//         orderEntryRepository.save(initialEntry);

//         kafkaTemplate.send("invoice-issued-topic", invoiceIssued);
//         TimeUnit.MILLISECONDS.sleep(100);

//         // Validate OrderEntry creation
//         List<OrderEntry> entries = orderEntryRepository.findByCustomerIdAndOrderId(
//                 customerCheckout.getCustomerId(), invoiceIssued.getOrderId());

//         assertNotNull(entries);
//         assertEquals(1, entries.size());
//         assertEquals(OrderStatus.INVOICED, entries.get(0).getOrderStatus());
//     }

//     @Test
//     public void testProcessShipmentNotification() throws InterruptedException {
//         CustomerCheckout customerCheckout = new CustomerCheckout();
//         customerCheckout.setCustomerId(1);

//         OrderEntryId orderEntryId = new OrderEntryId(
//                 customerCheckout.getCustomerId(),
//                 1001, // Order ID
//                 1, // Seller ID
//                 2001 // Product ID
//         );

//         OrderEntry initialEntry = new OrderEntry();
//         initialEntry.setId(orderEntryId);
//         initialEntry.setOrderStatus(OrderStatus.INVOICED); // Initial status
//         orderEntryRepository.save(initialEntry);
//         ShipmentNotification shipmentNotification = new ShipmentNotification();
//         shipmentNotification.setOrderId(1001);
//         shipmentNotification.setCustomerId(1);
//         shipmentNotification.setStatus(ShipmentStatus.APPROVED);

//         kafkaTemplate.send("shipment-notification-topic", shipmentNotification);
//         TimeUnit.MILLISECONDS.sleep(100);

//         List<OrderEntry> entries = orderEntryRepository.findByCustomerIdAndOrderId(
//                 shipmentNotification.getCustomerId(), shipmentNotification.getOrderId());

//         assertNotNull(entries);
//         assertEquals(1, entries.size());
//         assertEquals(OrderStatus.READY_FOR_SHIPMENT, entries.get(0).getOrderStatus());
//     }

 

// }
