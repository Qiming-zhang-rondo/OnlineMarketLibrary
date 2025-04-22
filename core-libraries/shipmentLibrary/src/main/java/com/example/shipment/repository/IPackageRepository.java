package com.example.shipment.repository;

import com.example.shipment.model.Package;
import com.example.common.entities.PackageStatus;
import com.example.shipment.model.Shipment;

import java.util.List;

public interface IPackageRepository {

    /**
     * 获取每个卖家中状态为指定值的订单中最早的订单号。
     * 返回的 Object[] 数组中，第一个元素为 sellerId，
     * 第二个元素为最早订单号的字符串（由 customerId 与 orderId 拼接而成）。
     */
    List<Object[]> getOldestOpenShipmentPerSeller(PackageStatus status);

    /**
     * 根据 customerId、orderId、sellerId 以及状态查询订单中所有的包裹。
     */
    List<Package> getShippedPackagesByOrderAndSeller(int customerId, int orderId, int sellerId, PackageStatus status);

    /**
     * 获取指定 customerId 与 orderId 的订单中状态为指定值的包裹数量。
     */
    int getTotalDeliveredPackagesForOrder(int customerId, int orderId, PackageStatus status);

    /**
     * 查询指定 customerId 与 orderId 的所有包裹。
     */
    List<Package> findAllByOrderIdAndCustomerId(int customerId, int orderId);

    /**
     * 清空包裹数据（例如用于测试或重置数据）。
     */
    void deleteAll();

    /**
     * 保存或更新 Shipment
     */
    void savePackage(Package Package);

    void saveAll(List<Package> Packages);


    void save(Package pack);

}

