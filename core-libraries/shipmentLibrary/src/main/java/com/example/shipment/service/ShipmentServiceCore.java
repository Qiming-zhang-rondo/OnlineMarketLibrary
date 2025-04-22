package com.example.shipment.service;

import com.example.common.entities.OrderItem;
import com.example.common.entities.PackageStatus;
import com.example.common.entities.ShipmentStatus;
import com.example.common.events.DeliveryNotification;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.ShipmentNotification;
import com.example.common.driver.MarkStatus;
import com.example.common.driver.TransactionMark;
import com.example.common.driver.TransactionType;
//import com.example.shipment.config.IShipmentConfig;
import com.example.common.messaging.IEventPublisher;
import com.example.shipment.model.Package;
import com.example.shipment.model.PackageId;
import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentId;
import com.example.shipment.repository.IPackageRepository;
import com.example.shipment.repository.IShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShipmentServiceCore implements IShipmentService {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentServiceCore.class);

    private final IShipmentRepository shipmentRepository;
    private final IPackageRepository packageRepository;
    private final IEventPublisher eventPublisher;
//    private final IShipmentConfig config;  // 可选

    public ShipmentServiceCore(IShipmentRepository shipmentRepository,
                               IPackageRepository packageRepository,
                               IEventPublisher eventPublisher) {
//                               IShipmentConfig config
        this.shipmentRepository = shipmentRepository;
        this.packageRepository = packageRepository;
        this.eventPublisher = eventPublisher;
//        this.config = config;
    }

    /**
     * 处理 PaymentConfirmed 事件，生成 Shipment 和对应的 Package，并发送事件
     */
    public void processShipment(PaymentConfirmed paymentConfirmed) {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Starting shipment processing for Order ID: {}, Customer ID: {}",
                paymentConfirmed.getOrderId(), paymentConfirmed.getCustomer().getCustomerId());

        // 组装 Shipment 对象
        Shipment shipment = new Shipment();
        ShipmentId shipmentId = new ShipmentId(paymentConfirmed.getCustomer().getCustomerId(),
                paymentConfirmed.getOrderId());
        shipment.setId(shipmentId);
        shipment.setPackageCount(paymentConfirmed.getItems().size());
        shipment.setTotalFreightValue(
                (float) paymentConfirmed.getItems().stream().mapToDouble(OrderItem::getFreightValue).sum());
        shipment.setRequestDate(now);
        shipment.setStatus(ShipmentStatus.APPROVED);
        // 设置客户地址等信息
        shipment.setFirstName(paymentConfirmed.getCustomer().getFirstName());
        shipment.setLastName(paymentConfirmed.getCustomer().getLastName());
        shipment.setStreet(paymentConfirmed.getCustomer().getStreet());
        shipment.setComplement(paymentConfirmed.getCustomer().getComplement());
        shipment.setZipCode(paymentConfirmed.getCustomer().getZipCode());
        shipment.setCity(paymentConfirmed.getCustomer().getCity());
        shipment.setState(paymentConfirmed.getCustomer().getState());

        logger.info("Saving shipment: {}", shipmentId);
        try {
            shipmentRepository.save(shipment);
        } catch (Exception e) {
            logger.error("Error saving shipment for Order ID: {}, Exception: {}",
                    paymentConfirmed.getOrderId(), e.getMessage(), e);
        }

        // 生成 Package 列表
        int packageIdCounter = 1;
        List<Package> packageList = new ArrayList<>();
        for (OrderItem item : paymentConfirmed.getItems()) {
            Package pkg = new Package();
            PackageId pkgId = new PackageId(paymentConfirmed.getCustomer().getCustomerId(),
                    paymentConfirmed.getOrderId(), packageIdCounter);
            pkg.setId(pkgId);
            pkg.setStatus(PackageStatus.SHIPPED);
            pkg.setFreightValue(item.getFreightValue());
            pkg.setShippingDate(now);
            pkg.setSellerId(item.getSellerId());
            pkg.setProductId(item.getProductId());
            pkg.setProductName(item.getProductName());
            pkg.setQuantity(item.getQuantity());
            packageList.add(pkg);
            packageIdCounter++;
        }
        packageRepository.saveAll(packageList);

        // 发送 ShipmentNotification 事件
        ShipmentNotification shipmentNotification = new ShipmentNotification(
                paymentConfirmed.getCustomer().getCustomerId(),
                paymentConfirmed.getOrderId(),
                now,
                paymentConfirmed.getInstanceId(),
                ShipmentStatus.APPROVED);
//        eventPublisher.sendShipmentNotification(shipmentNotification);
          eventPublisher.publishEvent("shipment-notification-topic", shipmentNotification);

        // 发送 TransactionMark 事件
        TransactionMark transactionMark = new TransactionMark(
                paymentConfirmed.getInstanceId(),
                TransactionType.CUSTOMER_SESSION,
                paymentConfirmed.getCustomer().getCustomerId(),
                MarkStatus.SUCCESS,
                "shipment");
        eventPublisher.publishEvent("TransactionMark_CUSTOMER_SESSION", transactionMark);
    }

    /**
     * 处理错误场景时的补救措施，发送 POISON 事件
     */
    public void processPoisonShipment(PaymentConfirmed paymentConfirmed) {
        TransactionMark transactionMark = new TransactionMark(
                paymentConfirmed.getInstanceId(),
                TransactionType.CUSTOMER_SESSION,
                paymentConfirmed.getCustomer().getCustomerId(),
                MarkStatus.ABORT,
                "shipment");
        eventPublisher.publishEvent("TransactionMark_CUSTOMER_SESSION", transactionMark);
    }

    /**
     * 根据指定 instanceId 查询并更新包裹和 Shipment 状态，
     * 例如将 Shipment 状态从 APPROVED 更新为 DELIVERY_IN_PROGRESS 或 CONCLUDED，
     * 并为每个包裹发送 DeliveryNotification。
     */
    public void updateShipment(String instanceId) throws Exception {
        logger.info("Starting updateShipment for instanceId: {}", instanceId);
        // 查询待更新的最早包裹数据（由包裹仓库提供接口）
        List<Object[]> oldestShipments = packageRepository.getOldestOpenShipmentPerSeller(PackageStatus.SHIPPED);
        logger.info("Found {} oldest shipments to process.", oldestShipments.size());

        for (Object[] shipmentData : oldestShipments) {
            Integer sellerId = (Integer) shipmentData[0];
            String orderDetails = (String) shipmentData[1];
            if (orderDetails != null) {
                String[] ids = orderDetails.split("\\|");
                if (ids.length == 2) {
                    int customerId = Integer.parseInt(ids[0]);
                    int orderId = Integer.parseInt(ids[1]);
                    List<Package> shippedPackages = packageRepository.getShippedPackagesByOrderAndSeller(
                            customerId, orderId, sellerId, PackageStatus.SHIPPED);
                    if (shippedPackages.isEmpty()) {
                        logger.warn("No packages for seller ID {} and order ID {}", sellerId, orderId);
                        continue;
                    }
                    updatePackageDelivery(shippedPackages, instanceId);
                } else {
                    logger.warn("Incomplete order details for seller ID {}. Skipping.", sellerId);
                }
            }
        }
        logger.info("Completed updateShipment for instanceId: {}", instanceId);
    }

    /**
     * 更新包裹的交付状态，并发送 DeliveryNotification 事件，
     * 同时根据包裹数量更新 Shipment 状态。
     */
    private void updatePackageDelivery(List<Package> sellerPackages, String instanceId) throws Exception {
        int customerId = sellerPackages.get(0).getCustomerId();
        int orderId = sellerPackages.get(0).getOrderId();
        ShipmentId shipmentId = new ShipmentId(customerId, orderId);
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new Exception("Shipment " + customerId + "-" + orderId + " not found"));

        LocalDateTime now = LocalDateTime.now();

        // 如 Shipment 状态为 APPROVED，则更新为 DELIVERY_IN_PROGRESS
        if (shipment.getStatus() == ShipmentStatus.APPROVED) {
            shipment.setStatus(ShipmentStatus.DELIVERY_IN_PROGRESS);
            shipmentRepository.save(shipment);
            ShipmentNotification notification = new ShipmentNotification(
                    shipment.getCustomerId(), shipment.getOrderId(), now, instanceId,
                    ShipmentStatus.DELIVERY_IN_PROGRESS);
            eventPublisher.publishEvent("shipment-notification-topic", notification);
        }

        // 获取当前已交付包裹数量
        int countDelivered = packageRepository.getTotalDeliveredPackagesForOrder(customerId, orderId, PackageStatus.DELIVERED);
        // 更新每个包裹的状态为 DELIVERED，并发送 DeliveryNotification 事件
        for (Package pack : sellerPackages) {
            pack.setStatus(PackageStatus.DELIVERED);
            pack.setDeliveryDate(now);
            packageRepository.save(pack);
            DeliveryNotification delivery = new DeliveryNotification(
                    shipment.getCustomerId(), pack.getOrderId(), pack.getPackageId(),
                    pack.getSellerId(), pack.getProductId(), pack.getProductName(),
                    PackageStatus.DELIVERED, now, instanceId);
            //创建新方法 在eventPublisher中
            eventPublisher.publishEvent("delivery-notification-topic",delivery);
        }
        packageRepository.saveAll(sellerPackages);

        // 判断是否所有包裹都已交付，如果是则更新 Shipment 状态为 CONCLUDED
        if (shipment.getPackageCount() == countDelivered + sellerPackages.size()) {
            shipment.setStatus(ShipmentStatus.CONCLUDED);
            shipmentRepository.save(shipment);
            ShipmentNotification notification = new ShipmentNotification(
                    shipment.getCustomerId(), shipment.getOrderId(), now, instanceId,
                    ShipmentStatus.CONCLUDED);
            eventPublisher.publishEvent("shipment-notification-topic", notification);
        }
    }

    /**
     * 清空所有 Shipment 数据（例如测试或重置时使用）
     */
    public void cleanup() {
        shipmentRepository.deleteAll();
    }
}

