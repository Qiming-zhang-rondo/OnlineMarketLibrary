package com.example.order.service;

import com.example.common.driver.MarkStatus;
import com.example.common.driver.TransactionMark;
import com.example.common.driver.TransactionType;
import com.example.common.entities.CartItem;
import com.example.common.entities.OrderStatus;
import com.example.common.entities.ShipmentStatus;
import com.example.common.events.*;
import com.example.order.kafka.OrderKafkaProducer;
import com.example.order.model.*;
import com.example.order.repository.CustomerOrderRepository;
import com.example.order.repository.OrderHistoryRepository;
import com.example.order.repository.OrderItemRepository;
import com.example.order.repository.OrderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.time.format.DateTimeFormatter;

@Service
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final OrderKafkaProducer orderKafkaProducer; 
    private Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrderHistoryRepository orderHistoryRepository,
            CustomerOrderRepository customerOrderRepository,
            OrderKafkaProducer orderKafkaProducer) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.orderKafkaProducer = orderKafkaProducer; 

    }

    @Override
    @Transactional
    public CompletableFuture<Void> processStockConfirmed(StockConfirmed checkout) {
        logger.info("enter CompletableFuture<Void> processStockConfirmed");
        return CompletableFuture.runAsync(() -> {
            try {
                LocalDateTime now = LocalDateTime.now();
                logger.info("Start processing StockConfirmed for customer ID: {}",
                        checkout.getCustomerCheckout().getCustomerId());

                // Calculate the total freight and order amount
                float totalFreight = 0;
                float totalAmount = 0;
                for (CartItem item : checkout.getItems()) {
                    logger.info("Processing item with Product ID: {}, Quantity: {}", item.getProductId(),
                            item.getQuantity());
                    totalFreight += item.getFreightValue();
                    totalAmount += (item.getUnitPrice() * item.getQuantity());
                }
                logger.info("Total freight: {}, Total amount before vouchers: {}", totalFreight, totalAmount);

                float totalItems = totalAmount;
                float totalIncentive = 0;
                Map<Map.Entry<Integer, Integer>, Float> totalPerItem = new HashMap<>();

                for (CartItem item : checkout.getItems()) {
                    float totalItem = item.getUnitPrice() * item.getQuantity();
                    float voucher = Math.min(totalItem, item.getVoucher());

                    totalAmount -= voucher;
                    totalIncentive += voucher;
                    totalItem -= voucher;
                    totalPerItem.put(new AbstractMap.SimpleEntry<>(item.getSellerId(), item.getProductId()), totalItem);

                    logger.info(
                            "Item processed - Product ID: {}, Seller ID: {}, Voucher Applied: {}, Remaining Amount: {}",
                            item.getProductId(), item.getSellerId(), voucher, totalItem);
                }
                logger.info("Total amount after vouchers: {}, Total incentive applied: {}", totalAmount,
                        totalIncentive);

                // Get or create a customer order
                CustomerOrder customerOrder = customerOrderRepository
                        .findByCustomerId(checkout.getCustomerCheckout().getCustomerId());
                if (customerOrder == null) {
                    customerOrder = new CustomerOrder();
                    customerOrder.setCustomerId(checkout.getCustomerCheckout().getCustomerId());
                    customerOrder.setNextOrderId(1);
                    customerOrderRepository.save(customerOrder);
                    logger.info("New customer order created for customer ID: {}",
                            checkout.getCustomerCheckout().getCustomerId());
                } else {
                    customerOrder.setNextOrderId(customerOrder.getNextOrderId() + 1);
                    customerOrderRepository.save(customerOrder);
                    logger.info("Existing customer order updated for customer ID: {}, Next order ID: {}",
                            checkout.getCustomerCheckout().getCustomerId(), customerOrder.getNextOrderId());
                }

                String invoiceNumber = String.format("%d-%s-%03d",
                        checkout.getCustomerCheckout().getCustomerId(),
                        now.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                        customerOrder.getNextOrderId());
                logger.info("Generated invoice number: {}", invoiceNumber);

                Order order = new Order();
                OrderId orderId = new OrderId();
                orderId.setCustomerId(checkout.getCustomerCheckout().getCustomerId());
                orderId.setOrderId(customerOrder.getNextOrderId());
                order.setId(orderId);
                order.setInvoiceNumber(invoiceNumber);
                order.setStatus(OrderStatus.INVOICED);
                order.setPurchaseDate(now);
                order.setTotalAmount(totalAmount);
                order.setTotalItems(totalItems);
                order.setTotalFreight(totalFreight);
                order.setTotalIncentive(totalIncentive);
                order.setTotalInvoice(totalAmount + totalFreight);
                order.setCountItems(checkout.getItems().size());
                order.setCreatedAt(now);
                order.setUpdatedAt(now);
                orderRepository.save(order);
                logger.info("Order saved with ID: {} for customer ID: {} for status: {}", order.getOrderId(),
                        order.getCustomerId(), order.getStatus());

                List<com.example.common.entities.OrderItem> commonOrderItems = new ArrayList<>();
                int itemId = 1;
                for (CartItem item : checkout.getItems()) {
                    OrderItem orderItem = new OrderItem();
                    OrderItemId orderItemId = new OrderItemId();
                    orderItemId.setCustomerId(checkout.getCustomerCheckout().getCustomerId());
                    orderItemId.setOrderId(customerOrder.getNextOrderId());
                    orderItemId.setOrderItemId(itemId++);
                    orderItem.setId(orderItemId);
                    orderItem.setProductId(item.getProductId());
                    orderItem.setProductName(item.getProductName());
                    orderItem.setSellerId(item.getSellerId());
                    orderItem.setUnitPrice(item.getUnitPrice());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setTotalItems(item.getUnitPrice() * item.getQuantity());
                    orderItem.setTotalAmount(
                            totalPerItem.get(new AbstractMap.SimpleEntry<>(item.getSellerId(), item.getProductId())));
                    orderItem.setFreightValue(item.getFreightValue());
                    orderItem.setShippingLimitDate(now.plusDays(3));
                    orderItemRepository.save(orderItem);

                    com.example.common.entities.OrderItem commonOrderItem = new com.example.common.entities.OrderItem();
                    commonOrderItem.setOrderId(orderItem.getOrderId());
                    commonOrderItem.setOrderItemId(orderItem.getOrderItemId());
                    commonOrderItem.setProductId(orderItem.getProductId());
                    commonOrderItem.setProductName(orderItem.getProductName());
                    commonOrderItem.setSellerId(orderItem.getSellerId());
                    commonOrderItem.setUnitPrice(orderItem.getUnitPrice());
                    commonOrderItem.setQuantity(orderItem.getQuantity());
                    commonOrderItem.setTotalItems(orderItem.getTotalItems());
                    commonOrderItem.setTotalAmount(orderItem.getTotalAmount());
                    commonOrderItem.setFreightValue(orderItem.getFreightValue());
                    commonOrderItem.setShippingLimitDate(orderItem.getShippingLimitDate());
                    commonOrderItems.add(commonOrderItem);
                    logger.info("Order item saved - Product ID: {}, Order Item ID: {}", orderItem.getProductId(),
                            orderItem.getOrderItemId());
                }

                OrderHistoryId orderHistoryId = new OrderHistoryId();
                orderHistoryId.setCustomerId(order.getCustomerId());
                orderHistoryId.setOrderId(order.getOrderId());
                OrderHistory orderHistory = new OrderHistory();
                orderHistory.setId(orderHistoryId);
                orderHistory.setCreatedAt(now);
                orderHistory.setStatus(OrderStatus.INVOICED);
                orderHistoryRepository.save(orderHistory);
                logger.info("Order history saved for order ID: {}", order.getOrderId());

                InvoiceIssued invoiceIssued = new InvoiceIssued(
                        checkout.getCustomerCheckout(),
                        customerOrder.getNextOrderId(),
                        invoiceNumber,
                        now,
                        order.getTotalInvoice(),
                        commonOrderItems,
                        checkout.getInstanceId());

                orderKafkaProducer.sendInvoiceIssued(invoiceIssued);
                logger.info("InvoiceIssued event sent for customer ID: {}",
                        checkout.getCustomerCheckout().getCustomerId());

            } catch (Exception e) {
                logger.error("Failed to process stock confirmation: {}", e.getMessage(), e);
                throw new RuntimeException("Invoiced issued send failed", e);
            }
        });
    }

    @Override
    @Transactional
    public void processPaymentConfirmed(PaymentConfirmed paymentConfirmed) {
        LocalDateTime now = LocalDateTime.now();

        // find order
        Order order = orderRepository
                .findByCustomerIdAndOrderId(paymentConfirmed.getCustomer().getCustomerId(),
                        paymentConfirmed.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot find order " + paymentConfirmed.getCustomer().getCustomerId() + "-"
                                + paymentConfirmed.getOrderId()));

        // update status
        order.setStatus(OrderStatus.PAYMENT_PROCESSED);
        order.setPaymentDate(paymentConfirmed.getDate());
        order.setUpdatedAt(now);


        orderRepository.save(order);

        // save order history
        OrderHistoryId orderHistoryId = new OrderHistoryId();
        orderHistoryId.setCustomerId(paymentConfirmed.getCustomer().getCustomerId());
        orderHistoryId.setOrderId(paymentConfirmed.getOrderId());
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setId(orderHistoryId);
        orderHistory.setCreatedAt(now);
        orderHistory.setStatus(OrderStatus.PAYMENT_PROCESSED);
        orderHistory.setOrder(order); 

        orderHistoryRepository.save(orderHistory);

   
        orderRepository.flush();
    }

    @Override
    @Transactional
    public void processPaymentFailed(PaymentFailed paymentFailed) {
        LocalDateTime now = LocalDateTime.now();
        // logger.info("Entered processPaymentFailed method at {}", now);

        Order order = orderRepository
                .findByCustomerIdAndOrderId(paymentFailed.getCustomer().getCustomerId(), paymentFailed.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot find order " + paymentFailed.getCustomer().getCustomerId() + "-"
                                + paymentFailed.getOrderId()));



  
        order.setStatus(OrderStatus.PAYMENT_FAILED);
        order.setUpdatedAt(now);


        orderRepository.save(order);
        // logger.info("Order status updated and saved for order ID: {}",
        // paymentFailed.getOrderId());


        OrderHistoryId orderHistoryId = new OrderHistoryId();
        orderHistoryId.setCustomerId(paymentFailed.getCustomer().getCustomerId());
        orderHistoryId.setOrderId(paymentFailed.getOrderId());

        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setId(orderHistoryId);
        orderHistory.setCreatedAt(now);
        orderHistory.setStatus(OrderStatus.PAYMENT_FAILED);
        orderHistory.setOrder(order); 
        orderHistoryRepository.save(orderHistory);

        orderRepository.flush();
        // logger.info("Order and history flush completed for order ID: {}",
        // paymentFailed.getOrderId());
    }

    @Override
    @Transactional
    public void processShipmentNotification(ShipmentNotification shipmentNotification) {
        LocalDateTime now = LocalDateTime.now();

        // Looks for the order and throws an exception if it cannot be found
        Order order = orderRepository
                .findByCustomerIdAndOrderId(shipmentNotification.getCustomerId(), shipmentNotification.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot find order " + shipmentNotification.getCustomerId() + "-"
                                + shipmentNotification.getOrderId()));

        // confirm order status
        OrderStatus orderStatus = OrderStatus.READY_FOR_SHIPMENT;
        if (shipmentNotification.getStatus() == ShipmentStatus.DELIVERY_IN_PROGRESS) {
            orderStatus = OrderStatus.IN_TRANSIT;
            order.setDeliveredCarrierDate(shipmentNotification.getEventDate());
        } else if (shipmentNotification.getStatus() == ShipmentStatus.CONCLUDED) {
            orderStatus = OrderStatus.DELIVERED;
            order.setDeliveredCustomerDate(shipmentNotification.getEventDate());
        }

        // update order history
        OrderHistoryId orderHistoryId = new OrderHistoryId();
        orderHistoryId.setCustomerId(shipmentNotification.getCustomerId());
        orderHistoryId.setOrderId(shipmentNotification.getOrderId());
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setId(orderHistoryId);
        orderHistory.setCreatedAt(now);
        orderHistory.setStatus(orderStatus);
        orderHistory.setOrder(order);

        // update order status
        order.setStatus(orderStatus);
        order.setUpdatedAt(now);

        //Save the order and order history
        orderRepository.save(order);
        orderHistoryRepository.save(orderHistory);

       
        orderRepository.flush();
    }

    @Override
    public void cleanup() {
       
        orderRepository.deleteAll();
    }

    @Override
    public CompletableFuture<Void> processPoisonStockConfirmed(StockConfirmed stockConfirmed) {
        // Create an asynchronous task
        return CompletableFuture.runAsync(() -> {
            // asynchronously process the business logic for inventory validation failures
            TransactionMark transactionMark = new TransactionMark(
                    stockConfirmed.getInstanceId(),
                    TransactionType.CUSTOMER_SESSION,
                    stockConfirmed.getCustomerCheckout().getCustomerId(),
                    MarkStatus.ABORT,
                    "order");

            // Send transaction tags asynchronously using Kafka producer
            orderKafkaProducer.sendTransactionMark(transactionMark);
        });
    }

}
