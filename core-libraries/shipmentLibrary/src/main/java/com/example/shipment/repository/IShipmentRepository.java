package com.example.shipment.repository;

import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentId;

import java.util.Optional;

public interface IShipmentRepository {

    /**
     * 根据 ShipmentId 查询
     */
    Optional<Shipment> findById(ShipmentId id);

    /**
     * 保存或更新 Shipment
     */

    void save(Shipment shipment);

    /**
     * 根据实体删除
     */
    void deleteShipment(Shipment shipment);

    /**
     * 清空表数据
     */
    void deleteAll();


}

