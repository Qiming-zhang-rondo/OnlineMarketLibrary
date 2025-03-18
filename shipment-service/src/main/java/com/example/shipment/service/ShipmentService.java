package com.example.shipment.service;

import com.example.common.entities.ShipmentStatus;
import com.example.common.events.DeliveryNotification;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.ShipmentNotification;
import com.example.shipment.config.ShipmentConfig;
import com.example.common.driver.MarkStatus;
import com.example.common.driver.TransactionMark;
import com.example.common.driver.TransactionType;
import com.example.common.entities.OrderItem;
import com.example.common.entities.PackageStatus;
import com.example.shipment.kafka.ShipmentKafkaProducer;
import com.example.shipment.model.Package;
import com.example.shipment.model.PackageId;
import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentId;
import com.example.shipment.repository.PackageRepository;
import com.example.shipment.repository.ShipmentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ShipmentService {

        private static final Logger logger = LoggerFactory.getLogger(ShipmentService.class);

        @Autowired
        private ShipmentRepository shipmentRepository;

        @Autowired
        private PackageRepository packageRepository;

        @Autowired
        private ShipmentKafkaProducer shipmentKafkaProducer; 

        @Autowired
        private ShipmentConfig config;

        @Transactional
        public void processShipment(PaymentConfirmed paymentConfirmed) {
                try {
                        LocalDateTime now = LocalDateTime.now();
                        logger.info("Starting shipment processing for Order ID: {}, Customer ID: {}",
                                        paymentConfirmed.getOrderId(),
                                        paymentConfirmed.getCustomer().getCustomerId());

                        // Create and set composite primary key for Shipment
                        Shipment shipment = new Shipment();
                        ShipmentId shipmentId = new ShipmentId(paymentConfirmed.getCustomer().getCustomerId(),
                                        paymentConfirmed.getOrderId());
                        shipment.setId(shipmentId);
                        shipment.setPackageCount(paymentConfirmed.getItems().size());
                        shipment.setTotalFreightValue(
                                        (float) paymentConfirmed.getItems().stream()
                                                        .mapToDouble(OrderItem::getFreightValue).sum());
                        shipment.setRequestDate(now);
                        shipment.setStatus(ShipmentStatus.APPROVED);
                        shipment.setFirstName(paymentConfirmed.getCustomer().getFirstName());
                        shipment.setLastName(paymentConfirmed.getCustomer().getLastName());
                        shipment.setStreet(paymentConfirmed.getCustomer().getStreet());
                        shipment.setComplement(paymentConfirmed.getCustomer().getComplement());
                        shipment.setZipCode(paymentConfirmed.getCustomer().getZipCode());
                        shipment.setCity(paymentConfirmed.getCustomer().getCity());
                        shipment.setState(paymentConfirmed.getCustomer().getState());

                        logger.info("Saving shipment with ID: {} for Order ID: {}, with package count is {}",
                                        shipmentId,
                                        paymentConfirmed.getOrderId(), shipment.getPackageCount());

                        logger.info(
                                        "Shipment details before save: ID: {}, Package Count: {}, Total Freight: {}, Request Date: {}, Status: {}, First Name: {}",
                                        shipmentId, shipment.getPackageCount(), shipment.getTotalFreightValue(),
                                        shipment.getRequestDate(), shipment.getStatus(), shipment.getFirstName());

                        try {
                                logger.info("enter try");
                                shipmentRepository.save(shipment);
                                logger.info("Successfully saved shipment.");
                        } catch (Exception e) {
                                logger.error("Error saving shipment for Order ID: {}, Exception: {}",
                                                paymentConfirmed.getOrderId(),
                                                e.getMessage(), e);
                        }

                        
                        int packageId = 1;
                        List<Package> packageList = new ArrayList<>();
                        for (OrderItem item : paymentConfirmed.getItems()) {
                                logger.info("enter packagelist");
                                Package pkg = new Package();
                                PackageId pkgId = new PackageId(paymentConfirmed.getCustomer().getCustomerId(),
                                                paymentConfirmed.getOrderId(), packageId);
                                pkg.setId(pkgId);
                                pkg.setStatus(PackageStatus.SHIPPED);
                                pkg.setFreightValue(item.getFreightValue());
                                pkg.setShippingDate(now);
                                pkg.setSellerId(item.getSellerId());
                                pkg.setProductId(item.getProductId());
                                pkg.setProductName(item.getProductName());
                                pkg.setQuantity(item.getQuantity());

                                logger.info("Adding package with ID: {} for Product ID: {}", pkgId,
                                                item.getProductId());
                                packageList.add(pkg);
                                packageId++;
                        }

                        logger.info("Saving all packages for Order ID: {}", paymentConfirmed.getOrderId());
                        packageRepository.saveAll(packageList);

                      

                        logger.info("sending ShipmentNotification for Order ID: {}",
                                        paymentConfirmed.getOrderId());
                        ShipmentNotification shipmentNotification = new ShipmentNotification(
                                        paymentConfirmed.getCustomer().getCustomerId(),
                                        paymentConfirmed.getOrderId(),
                                        now,
                                        paymentConfirmed.getInstanceId(),
                                        ShipmentStatus.APPROVED);
                        shipmentKafkaProducer.sendShipmentNotification(shipmentNotification);

                        TransactionMark transactionMark = new TransactionMark(
                                        paymentConfirmed.getInstanceId(),
                                        TransactionType.CUSTOMER_SESSION,
                                        paymentConfirmed.getCustomer().getCustomerId(),
                                        MarkStatus.SUCCESS,
                                        "shipment");
                        shipmentKafkaProducer.sendTransactionMark(transactionMark);

                } catch (Exception e) {
                        logger.error("Failed to process shipment for Order ID: {}", paymentConfirmed.getOrderId(), e);
                        throw new RuntimeException("Failed to process shipment", e);
                }
        }

        public void processPoisonShipment(PaymentConfirmed paymentConfirmed) {
                TransactionMark transactionMark = new TransactionMark(
                                paymentConfirmed.getInstanceId(),
                                TransactionType.CUSTOMER_SESSION,
                                paymentConfirmed.getCustomer().getCustomerId(),
                                MarkStatus.ABORT,
                                "shipment");

                // send TransactionMark to Kafka
                shipmentKafkaProducer.sendTransactionMark(transactionMark);
        }

        @Transactional
        public void updateShipment(String instanceId) throws Exception {
                logger.info("Starting updateShipment for instanceId: {}", instanceId);

                //Query uncompleted shipments for each seller's earliest shipped status
                List<Object[]> oldestShipments = packageRepository
                                .getOldestOpenShipmentPerSeller(PackageStatus.SHIPPED);
                logger.info("Found {} oldest shipments to process.", oldestShipments.size());

                for (Object[] shipmentData : oldestShipments) {
                        Integer sellerId = (Integer) shipmentData[0];
                        logger.info("Processing seller with ID: {}", sellerId);

                        // Treat orderDetails as a String instead of a String[]
                        String orderDetails = (String) shipmentData[1];
                        if (orderDetails != null) {
                               // split customerId and orderId using the split() method
                                String[] ids = orderDetails.split("\\|");
                                if (ids.length == 2) {
                                        int customerId = Integer.parseInt(ids[0]);
                                        int orderId = Integer.parseInt(ids[1]);
                                        logger.info("Retrieved order details - Customer ID: {}, Order ID: {}",
                                                        customerId, orderId);

                                  
                                        List<Package> shippedPackages = packageRepository
                                                        .getShippedPackagesByOrderAndSeller(customerId,
                                                                        orderId, sellerId, PackageStatus.SHIPPED);
                                        if (shippedPackages.isEmpty()) {
                                                logger.warn("No packages retrieved from the DB for seller ID {} and order ID {}",
                                                                sellerId,
                                                                orderId);
                                                continue;
                                        }
                                        logger.info("Found {} shipped packages for Customer ID: {}, Order ID: {}",
                                                        shippedPackages.size(),
                                                        customerId, orderId);

                                        
                                        logger.info("Updating delivery status for packages of Customer ID: {}, Order ID: {}",
                                                        customerId,
                                                        orderId);
                                        updatePackageDelivery(shippedPackages, instanceId);
                                } else {
                                        logger.warn("Order details for seller ID {} are incomplete or null. Skipping.",
                                                        sellerId);
                                }
                        }

                        logger.info("Completed updateShipment for instanceId: {}", instanceId);
                }
        }

        @Transactional
        private void updatePackageDelivery(List<Package> sellerPackages, String instanceId) throws Exception {
                int customerId = sellerPackages.get(0).getCustomerId();
                int orderId = sellerPackages.get(0).getOrderId();
                ShipmentId shipmentId = new ShipmentId(customerId, orderId);

                Shipment shipment = shipmentRepository.findById(shipmentId)
                                .orElseThrow(() -> new Exception(
                                                "Shipment ID " + customerId + "-" + orderId
                                                                + " cannot be found in the database!"));

                List<Runnable> tasks = new ArrayList<>(sellerPackages.size() + 1);
                LocalDateTime now = LocalDateTime.now();

                if (shipment.getStatus() == ShipmentStatus.APPROVED) {
                        shipment.setStatus(ShipmentStatus.DELIVERY_IN_PROGRESS);
                        shipmentRepository.save(shipment);
                        ShipmentNotification shipmentNotification = new ShipmentNotification(
                                        shipment.getCustomerId(), shipment.getOrderId(), now, instanceId,
                                        ShipmentStatus.DELIVERY_IN_PROGRESS);
                        tasks.add(() -> shipmentKafkaProducer.sendShipmentNotification(shipmentNotification));
                }

         
                int countDelivered = packageRepository.getTotalDeliveredPackagesForOrder(customerId, orderId,
                                PackageStatus.DELIVERED);

                logger.debug("Count delivery for shipment id {}: {} total of {}",
                                shipment.getOrderId(), countDelivered, shipment.getPackageCount());

                for (Package pack : sellerPackages) {
                        pack.setStatus(PackageStatus.DELIVERED);
                        pack.setDeliveryDate(now);
                        packageRepository.save(pack);

                        DeliveryNotification delivery = new DeliveryNotification(
                                        shipment.getCustomerId(), pack.getOrderId(), pack.getPackageId(),
                                        pack.getSellerId(),
                                        pack.getProductId(), pack.getProductName(), PackageStatus.DELIVERED, now,
                                        instanceId);

                        tasks.add(() -> shipmentKafkaProducer.sendDeliveryNotification(delivery));
                }

                packageRepository.saveAll(sellerPackages);

                if (shipment.getPackageCount() == countDelivered + sellerPackages.size()) {
                        logger.debug("Delivery concluded for shipment id {}", shipment.getOrderId());
                        shipment.setStatus(ShipmentStatus.CONCLUDED);
                        shipmentRepository.save(shipment);

                        ShipmentNotification shipmentNotification = new ShipmentNotification(
                                        shipment.getCustomerId(), shipment.getOrderId(), now, instanceId,
                                        ShipmentStatus.CONCLUDED);
                        tasks.add(() -> shipmentKafkaProducer.sendShipmentNotification(shipmentNotification));
                } else {
                        logger.debug("Delivery not yet concluded for shipment id {}: count {} of total {}",
                                        shipment.getOrderId(), countDelivered + sellerPackages.size(),
                                        shipment.getPackageCount());
                }

                tasks.forEach(Runnable::run);
        }

        public void Cleanup() {
                this.shipmentRepository.cleanup();
        }

}
