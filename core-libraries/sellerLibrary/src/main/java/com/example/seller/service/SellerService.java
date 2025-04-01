package com.example.seller.service;

import com.example.common.entities.OrderItem;
import com.example.common.entities.OrderStatus;
import com.example.common.entities.PackageStatus;
import com.example.common.entities.ShipmentStatus;
import com.example.common.events.*;
import com.example.seller.dto.SellerDashboard;
import com.example.seller.infra.SellerConfig;
import com.example.seller.model.OrderEntry;
import com.example.seller.model.OrderEntryId;
import com.example.seller.model.OrderSellerView;
import com.example.seller.repository.OrderEntryRepository;
import com.example.seller.repository.OrderSellerViewRepository;
import com.example.seller.repository.SellerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class SellerService implements ISellerService {

    private final SellerRepository sellerRepository;
    private final OrderEntryRepository orderEntryRepository;
    private final OrderSellerViewRepository orderSellerViewRepository;
    private final SellerConfig config;
    private final Logger logger = LoggerFactory.getLogger(SellerService.class);
    @Autowired
    private MaterializedViewService materializedViewService;

    @Autowired
    public SellerService(OrderSellerViewRepository orderSellerViewRepository, OrderEntryRepository orderEntryRepository,
            SellerRepository sellerRepository, SellerConfig config) {
        this.orderSellerViewRepository = orderSellerViewRepository;
        this.orderEntryRepository = orderEntryRepository;
        this.sellerRepository = sellerRepository;
        this.config = config;
    }

    @Override
    @Transactional
    public void processInvoiceIssued(InvoiceIssued invoiceIssued) {
      
        List<OrderItem> items = invoiceIssued.getItems();

        // Iterate through the invoice items and create OrderEntry for each item
        for (OrderItem item : items) { 
            OrderEntry orderEntry = new OrderEntry();
            orderEntry.setCustomerId(invoiceIssued.getCustomer().getCustomerId());
            orderEntry.setOrderId(invoiceIssued.getOrderId());
            orderEntry.setSellerId(item.getSellerId());
            orderEntry.setProductId(item.getProductId());
            orderEntry.setProductName(item.getProductName());
            orderEntry.setProductCategory(""); 
            orderEntry.setUnitPrice(item.getUnitPrice());
            orderEntry.setQuantity(item.getQuantity());
            orderEntry.setTotalItems(item.getTotalItems());
            orderEntry.setTotalAmount(item.getTotalAmount());
            orderEntry.setTotalInvoice(item.getTotalAmount() + item.getFreightValue());
            orderEntry.setTotalIncentive(item.getTotalIncentive());
            orderEntry.setFreightValue(item.getFreightValue());
            orderEntry.setOrderStatus(OrderStatus.INVOICED);
            orderEntry.setNaturalKey(
                    String.format("%d_%d", invoiceIssued.getCustomer().getCustomerId(), invoiceIssued.getOrderId()));

       
            orderEntryRepository.save(orderEntry);
        }

    }

    @Override
    @Transactional
    public void processShipmentNotification(ShipmentNotification shipmentNotification) {
        logger.info("Processing ShipmentNotification for Order ID: {}, Customer ID: {}, Status: {}",
                shipmentNotification.getOrderId(), shipmentNotification.getCustomerId(),
                shipmentNotification.getStatus());

        List<OrderEntry> entries = sellerRepository.findByCustomerIdAndOrderId(
                shipmentNotification.getCustomerId(), shipmentNotification.getOrderId());

        for (OrderEntry entry : entries) {
            if (shipmentNotification.getStatus() == ShipmentStatus.APPROVED) {
                entry.setOrderStatus(OrderStatus.READY_FOR_SHIPMENT);
                entry.setShipmentDate(shipmentNotification.getEventDate());
                entry.setDeliveryStatus(PackageStatus.READY_TO_SHIP);
                logger.info("Order ID: {} updated to READY_FOR_SHIPMENT and delivery status set to READY_TO_SHIP",
                        entry.getId().getOrderId());
            } else if (shipmentNotification.getStatus() == ShipmentStatus.DELIVERY_IN_PROGRESS) {
                entry.setOrderStatus(OrderStatus.IN_TRANSIT);
                entry.setDeliveryStatus(PackageStatus.SHIPPED);
                logger.info("Order ID: {} updated to IN_TRANSIT and delivery status set to SHIPPED",
                        entry.getId().getOrderId());
            } else if (shipmentNotification.getStatus() == ShipmentStatus.CONCLUDED) {
                entry.setOrderStatus(OrderStatus.DELIVERED);
                logger.info("Order ID: {} marked as DELIVERED", entry.getId().getOrderId());
            }
        }

        orderEntryRepository.saveAll(entries);
        logger.info("Order entries saved successfully for Order ID: {}", shipmentNotification.getOrderId());

    }

    @Override
    @Transactional
    public void processDeliveryNotification(DeliveryNotification deliveryNotification) {
       
        Optional<OrderEntry> optionalOrderEntry = orderEntryRepository.findById(new OrderEntryId(
                deliveryNotification.getCustomerId(),
                deliveryNotification.getOrderId(),
                deliveryNotification.getSellerId(),
                deliveryNotification.getProductId()));

   
        OrderEntry oe = optionalOrderEntry.orElseThrow(() -> new RuntimeException(
                "[ProcessDeliveryNotification] Cannot find respective order entry for order id "
                        + deliveryNotification.getOrderId() + " and product id "
                        + deliveryNotification.getProductId()));

     
        oe.setPackageId(deliveryNotification.getPackageId());
        oe.setDeliveryDate(deliveryNotification.getDeliveryDate());
        oe.setDeliveryStatus(deliveryNotification.getStatus());

        
        orderEntryRepository.save(oe);
    }

    @Override
    @Transactional
    public void processPaymentConfirmed(PaymentConfirmed paymentConfirmed) {
        List<OrderEntry> entries = sellerRepository.findByCustomerIdAndOrderId(
                paymentConfirmed.getCustomer().getCustomerId(),
                paymentConfirmed.getOrderId());
        for (OrderEntry entry : entries) {
            entry.setOrderStatus(OrderStatus.PAYMENT_PROCESSED);
        }
        orderEntryRepository.saveAll(entries);
    }

    // @Override
    // @Transactional
    // public void processPaymentFailed(PaymentFailed paymentFailed) {
    // List<OrderEntry> entries = sellerRepository.findByCustomerIdAndOrderId(
    // paymentFailed.getCustomer().getCustomerId(),
    // paymentFailed.getOrderId());
    // for (OrderEntry entry : entries) {
    // entry.setOrderStatus(OrderStatus.PAYMENT_FAILED);
    // }
    // orderEntryRepository.saveAll(entries);
    // }

    @Override
    @Transactional
    public void processPaymentFailed(PaymentFailed paymentFailed) {
        logger.info("Processing PaymentFailed event: {}", paymentFailed);

        List<OrderEntry> entries = orderEntryRepository.findByCustomerIdAndOrderId(
                paymentFailed.getCustomer().getCustomerId(),
                paymentFailed.getOrderId());
        logger.info("Found {} entries for customerId: {}, orderId: {}",
                entries.size(),
                paymentFailed.getCustomer().getCustomerId(),
                paymentFailed.getOrderId());

        if (entries.isEmpty()) {
            logger.warn("No entries found for the given customerId and orderId.");
        }

        for (OrderEntry entry : entries) {
            logger.info("Updating order status for entry: {}", entry);
            entry.setOrderStatus(OrderStatus.PAYMENT_FAILED);
        }
        orderEntryRepository.saveAll(entries);
        logger.info("PaymentFailed processing completed.");
    }

    @Override
    @Transactional(readOnly = true)
    public SellerDashboard queryDashboard(int sellerId) {
        
        try {
    
            OrderSellerView sellerView = materializedViewService.getSellerView(sellerId);
            logger.info("dashboard seller view: {}", sellerView);
        
       
            List<OrderEntry> orderEntries = orderEntryRepository.findAllBySellerId(sellerId);
            logger.info("dashboard order entry: {}", orderEntries);
        
          
            return new SellerDashboard(sellerView, orderEntries);
        } catch (Exception e) {
      
            logger.error("Error while querying dashboard for sellerId {}: {}", sellerId, e.getMessage(), e);
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
