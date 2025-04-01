package com.example.shipment.repository;

import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, ShipmentId> {

    
    @Modifying
    @Transactional
    @Query("DELETE FROM Shipment")
    void cleanup();
}
