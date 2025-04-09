package com.example.order.service;

import com.example.common.driver.MarkStatus;
import com.example.common.driver.TransactionMark;
import com.example.common.driver.TransactionType;
import com.example.common.entities.CartItem;
import com.example.common.entities.OrderStatus;
import com.example.common.entities.ShipmentStatus;
import com.example.common.events.*;
import com.example.order.model.*;
import com.example.order.repository.ICustomerOrderRepository;
import com.example.order.repository.IOrderHistoryRepository;
import com.example.order.repository.IOrderItemRepository;
import com.example.order.repository.IOrderRepository;
import com.example.common.messaging.IEventPublisher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class OrderServiceCore implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IOrderItemRepository orderItemRepository;
    private final IOrderHistoryRepository orderHistoryRepository;
    private final ICustomerOrderRepository customerOrderRepository;
    private final IEventPublisher eventPublisher;

    public OrderServiceCore(IOrderRepository orderRepository,
                            IOrderItemRepository orderItemRepository,
                            IOrderHistoryRepository orderHistoryRepository,
                            ICustomerOrderRepository customerOrderRepository,
                            IEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> processStockConfirmed(StockConfirmed checkout) {
        return CompletableFuture.runAsync(() -> {
            try {
                LocalDateTime now = LocalDateTime.now();

                float totalFreight = 0;
                float totalAmount = 0;
                for (CartItem item : checkout.getItems()) {
                    totalFreight += item.getFreightValue();
                    totalAmount += (item.getUnitPrice() * item.getQuantity());
                }

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
                }

                CustomerOrder customerOrder = customerOrderRepository
                        .findByCustomerId(checkout.getCustomerCheckout().getCustomerId());
                if (customerOrder == null) {
                    customerOrder = new CustomerOrder();
                    customerOrder.setCustomerId(checkout.getCustomerCheckout().getCustomerId());
                    customerOrder.setNextOrderId(1);
                    customerOrderRepository.save(customerOrder);
                } else {
                    customerOrder.setNextOrderId(customerOrder.getNextOrderId() + 1);
                    customerOrderRepository.save(customerOrder);
                }

                String invoiceNumber = String.format("%d-%s-%03d",
                        checkout.getCustomerCheckout().getCustomerId(),
                        now.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                        customerOrder.getNextOrderId());

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
                }

                OrderHistoryId orderHistoryId = new OrderHistoryId();
                orderHistoryId.setCustomerId(order.getCustomerId());
                orderHistoryId.setOrderId(order.getOrderId());
                OrderHistory orderHistory = new OrderHistory();
                orderHistory.setId(orderHistoryId);
                orderHistory.setCreatedAt(now);
                orderHistory.setStatus(OrderStatus.INVOICED);
                orderHistoryRepository.save(orderHistory);

                InvoiceIssued invoiceIssued = new InvoiceIssued(
                        checkout.getCustomerCheckout(),
                        customerOrder.getNextOrderId(),
                        invoiceNumber,
                        now,
                        order.getTotalInvoice(),
                        commonOrderItems,
                        checkout.getInstanceId());

                eventPublisher.publishEvent("invoice-issued-topic", invoiceIssued);

            } catch (Exception e) {
                throw new RuntimeException("Invoiced issued send failed", e);
            }
        });
    }

    @Override
    public void processPaymentConfirmed(PaymentConfirmed paymentConfirmed) {
        LocalDateTime now = LocalDateTime.now();

        Order order = orderRepository
                .findByCustomerIdAndOrderId(paymentConfirmed.getCustomer().getCustomerId(),
                        paymentConfirmed.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot find order " + paymentConfirmed.getCustomer().getCustomerId() + "-"
                                + paymentConfirmed.getOrderId()));

        order.setStatus(OrderStatus.PAYMENT_PROCESSED);
        order.setPaymentDate(paymentConfirmed.getDate());
        order.setUpdatedAt(now);

        orderRepository.save(order);

        OrderHistoryId orderHistoryId = new OrderHistoryId();
        orderHistoryId.setCustomerId(paymentConfirmed.getCustomer().getCustomerId());
        orderHistoryId.setOrderId(paymentConfirmed.getOrderId());
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setId(orderHistoryId);
        orderHistory.setCreatedAt(now);
        orderHistory.setStatus(OrderStatus.PAYMENT_PROCESSED);
        orderHistory.setOrder(order);

        orderHistoryRepository.save(orderHistory);
    }

    @Override
    public void processPaymentFailed(PaymentFailed paymentFailed) {
        LocalDateTime now = LocalDateTime.now();

        Order order = orderRepository
                .findByCustomerIdAndOrderId(paymentFailed.getCustomer().getCustomerId(), paymentFailed.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot find order " + paymentFailed.getCustomer().getCustomerId() + "-"
                                + paymentFailed.getOrderId()));

        order.setStatus(OrderStatus.PAYMENT_FAILED);
        order.setUpdatedAt(now);

        orderRepository.save(order);

        OrderHistoryId orderHistoryId = new OrderHistoryId();
        orderHistoryId.setCustomerId(paymentFailed.getCustomer().getCustomerId());
        orderHistoryId.setOrderId(paymentFailed.getOrderId());

        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setId(orderHistoryId);
        orderHistory.setCreatedAt(now);
        orderHistory.setStatus(OrderStatus.PAYMENT_FAILED);
        orderHistory.setOrder(order);
        orderHistoryRepository.save(orderHistory);

    }

    @Override
    public void processShipmentNotification(ShipmentNotification shipmentNotification) {
        LocalDateTime now = LocalDateTime.now();

        Order order = orderRepository
                .findByCustomerIdAndOrderId(shipmentNotification.getCustomerId(), shipmentNotification.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot find order " + shipmentNotification.getCustomerId() + "-"
                                + shipmentNotification.getOrderId()));

        OrderStatus orderStatus = OrderStatus.READY_FOR_SHIPMENT;
        if (shipmentNotification.getStatus() == ShipmentStatus.DELIVERY_IN_PROGRESS) {
            orderStatus = OrderStatus.IN_TRANSIT;
            order.setDeliveredCarrierDate(shipmentNotification.getEventDate());
        } else if (shipmentNotification.getStatus() == ShipmentStatus.CONCLUDED) {
            orderStatus = OrderStatus.DELIVERED;
            order.setDeliveredCustomerDate(shipmentNotification.getEventDate());
        }

        OrderHistoryId orderHistoryId = new OrderHistoryId();
        orderHistoryId.setCustomerId(shipmentNotification.getCustomerId());
        orderHistoryId.setOrderId(shipmentNotification.getOrderId());
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setId(orderHistoryId);
        orderHistory.setCreatedAt(now);
        orderHistory.setStatus(orderStatus);
        orderHistory.setOrder(order);

        order.setStatus(orderStatus);
        order.setUpdatedAt(now);

        orderRepository.save(order);
        orderHistoryRepository.save(orderHistory);
    }

    @Override
    public void cleanup() {
        orderItemRepository.deleteAll();;
        orderHistoryRepository.deleteAll();
        orderRepository.deleteAll();
        customerOrderRepository.deleteAll();
    }

    @Override
    public CompletableFuture<Void> processPoisonStockConfirmed(StockConfirmed stockConfirmed) {
        return CompletableFuture.runAsync(() -> {
            TransactionMark transactionMark = new TransactionMark(
                    stockConfirmed.getInstanceId(),
                    TransactionType.CUSTOMER_SESSION,
                    stockConfirmed.getCustomerCheckout().getCustomerId(),
                    MarkStatus.ABORT,
                    "order");

            eventPublisher.publishEvent("transaction-mark-topic", transactionMark);
        });
    }
}
