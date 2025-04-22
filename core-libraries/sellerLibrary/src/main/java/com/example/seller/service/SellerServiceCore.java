package com.example.seller.service;

import com.example.common.entities.OrderItem;
import com.example.common.entities.OrderStatus;
import com.example.common.entities.PackageStatus;
import com.example.common.entities.ShipmentStatus;
import com.example.common.events.DeliveryNotification;
import com.example.common.events.InvoiceIssued;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.common.events.ShipmentNotification;
import com.example.seller.dto.SellerDashboard;
//import com.example.seller.infra.SellerConfig;
import com.example.seller.model.OrderEntry;
import com.example.seller.model.OrderEntryId;
import com.example.seller.model.OrderSellerView;
import com.example.seller.repository.IOrderEntryRepository;
import com.example.seller.repository.ISellerRepository;
import com.example.seller.repository.IOrderSellerViewRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class SellerServiceCore implements ISellerService {

    private final ISellerRepository sellerRepository;
    private final IOrderEntryRepository orderEntryRepository;
    private final IOrderSellerViewRepository orderSellerViewRepository;
    private final IMaterializedViewService materializedViewService;
//    private final SellerConfig config;
    private final Logger logger = LoggerFactory.getLogger(SellerServiceCore.class);

    public SellerServiceCore(ISellerRepository sellerRepository,
                             IOrderEntryRepository orderEntryRepository,
                             IOrderSellerViewRepository orderSellerViewRepository,
                             IMaterializedViewService materializedViewService) {
//                             SellerConfig config
        this.sellerRepository = sellerRepository;
        this.orderEntryRepository = orderEntryRepository;
        this.orderSellerViewRepository = orderSellerViewRepository;
        this.materializedViewService = materializedViewService;
//        this.config = config;
    }

    @Override
    public void processInvoiceIssued(InvoiceIssued invoiceIssued) {
        List<OrderItem> items = invoiceIssued.getItems();
        for (OrderItem item : items) {
            OrderEntry orderEntry = new OrderEntry();
            orderEntry.setCustomerId(invoiceIssued.getCustomer().getCustomerId());
            orderEntry.setOrderId(invoiceIssued.getOrderId());
            orderEntry.setSellerId(item.getSellerId());
            orderEntry.setProductId(item.getProductId());
            orderEntry.setProductName(item.getProductName());
            // 其他属性赋值
            orderEntry.setUnitPrice(item.getUnitPrice());
            orderEntry.setQuantity(item.getQuantity());
            orderEntry.setTotalAmount(item.getTotalAmount());
            orderEntry.setTotalInvoice(item.getTotalAmount() + item.getFreightValue());
            orderEntry.setFreightValue(item.getFreightValue());
            orderEntry.setOrderStatus(OrderStatus.INVOICED);
            // 自然键构造
            orderEntry.setNaturalKey(String.format("%d_%d",
                    invoiceIssued.getCustomer().getCustomerId(),
                    invoiceIssued.getOrderId()));
            orderEntryRepository.save(orderEntry);
        }
    }

    @Override
    public void processShipmentNotification(ShipmentNotification shipmentNotification) {
        logger.info("Processing ShipmentNotification for Order ID: {}, Customer ID: {}, Status: {}",
                shipmentNotification.getOrderId(),
                shipmentNotification.getCustomerId(),
                shipmentNotification.getStatus());

        List<OrderEntry> entries = orderEntryRepository.findByCustomerIdAndOrderId(
                shipmentNotification.getCustomerId(), shipmentNotification.getOrderId());

        for (OrderEntry entry : entries) {
            if (shipmentNotification.getStatus() == ShipmentStatus.APPROVED) {
                entry.setOrderStatus(OrderStatus.READY_FOR_SHIPMENT);
                entry.setShipmentDate(shipmentNotification.getEventDate());
                entry.setDeliveryStatus(PackageStatus.READY_TO_SHIP);
            } else if (shipmentNotification.getStatus() == ShipmentStatus.DELIVERY_IN_PROGRESS) {
                entry.setOrderStatus(OrderStatus.IN_TRANSIT);
                entry.setDeliveryStatus(PackageStatus.SHIPPED);
            } else if (shipmentNotification.getStatus() == ShipmentStatus.CONCLUDED) {
                entry.setOrderStatus(OrderStatus.DELIVERED);
            }
        }
        orderEntryRepository.saveAll(entries);
        logger.info("Order entries saved successfully for Order ID: {}", shipmentNotification.getOrderId());
    }

    @Override
    public void processDeliveryNotification(DeliveryNotification deliveryNotification) {
        Optional<OrderEntry> optionalOrderEntry = orderEntryRepository.findById(new OrderEntryId(
                deliveryNotification.getCustomerId(),
                deliveryNotification.getOrderId(),
                deliveryNotification.getSellerId(),
                deliveryNotification.getProductId()));

        OrderEntry orderEntry = optionalOrderEntry.orElseThrow(() -> new RuntimeException(
                "[ProcessDeliveryNotification] Cannot find order entry for order id "
                        + deliveryNotification.getOrderId() + " and product id " + deliveryNotification.getProductId()));

        orderEntry.setPackageId(deliveryNotification.getPackageId());
        orderEntry.setDeliveryDate(deliveryNotification.getDeliveryDate());
        orderEntry.setDeliveryStatus(deliveryNotification.getStatus());

        orderEntryRepository.save(orderEntry);
    }


    @Override
    public void processPaymentConfirmed(PaymentConfirmed paymentConfirmed) {
        List<OrderEntry> entries = sellerRepository.findByCustomerIdAndOrderId(
                paymentConfirmed.getCustomer().getCustomerId(),
                paymentConfirmed.getOrderId());
        for (OrderEntry entry : entries) {
            entry.setOrderStatus(OrderStatus.PAYMENT_PROCESSED);
        }
        orderEntryRepository.saveAll(entries);
    }

    @Override
    public void processPaymentFailed(PaymentFailed paymentFailed) {
        logger.info("Processing PaymentFailed event: {}", paymentFailed);
        List<OrderEntry> entries = orderEntryRepository.findByCustomerIdAndOrderId(
                paymentFailed.getCustomer().getCustomerId(),
                paymentFailed.getOrderId());
        if (entries.isEmpty()) {
            logger.warn("No entries found for customerId: {}, orderId: {}",
                    paymentFailed.getCustomer().getCustomerId(), paymentFailed.getOrderId());
        }
        for (OrderEntry entry : entries) {
            entry.setOrderStatus(OrderStatus.PAYMENT_FAILED);
        }
        orderEntryRepository.saveAll(entries);
        logger.info("PaymentFailed processing completed.");
    }

    @Override
    public SellerDashboard queryDashboard(int sellerId) {
        try {
            OrderSellerView sellerView = materializedViewService.getSellerView(sellerId);
            logger.info("dashboard seller view: {}", sellerView);
            List<OrderEntry> orderEntries = orderEntryRepository.findAllBySellerId(sellerId);
            logger.info("dashboard order entries: {}", orderEntries);
            return new SellerDashboard(sellerView, orderEntries);
        } catch (Exception e) {
            logger.error("Error querying dashboard for sellerId {}: {}", sellerId, e.getMessage(), e);
            throw new RuntimeException("Failed to query seller dashboard", e);
        }
    }

    @Override
    public void cleanup() {
        sellerRepository.deleteAll();
        orderEntryRepository.deleteAll();
    }

    @Override
    public void reset() {
        orderEntryRepository.deleteAll();
    }
}