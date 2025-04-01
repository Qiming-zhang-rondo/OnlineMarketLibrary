package com.example.shipment.repository;

import com.example.shipment.model.Package;
import com.example.shipment.model.PackageId;
import com.example.common.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, PackageId> {

  
    // get oldest open shipment
    @Query("SELECT p1.sellerId, MIN(CONCAT(p1.id.customerId, '|', p1.id.orderId)) " +
    "FROM Package p1 " +
    "WHERE p1.status = :status " +
    "GROUP BY p1.sellerId, p1.id.customerId, p1.id.orderId " +
    "ORDER BY MIN(CONCAT(p1.id.customerId, '|', p1.id.orderId))")
List<Object[]> getOldestOpenShipmentPerSeller(@Param("status") PackageStatus status);



@Query("SELECT p FROM Package p WHERE p.id.customerId = :customerId AND p.id.orderId = :orderId AND p.sellerId = :sellerId AND p.status = :status")
List<Package> getShippedPackagesByOrderAndSeller(@Param("customerId") int customerId, 
                                          @Param("orderId") int orderId, 
                                          @Param("sellerId") int sellerId, 
                                          @Param("status") PackageStatus status);

// get the total number of packages that have been delivered for a specified customer order
@Query("SELECT COUNT(p) FROM Package p WHERE p.id.customerId = :customerId AND p.id.orderId = :orderId AND p.status = :status")
int getTotalDeliveredPackagesForOrder(@Param("customerId") int customerId, 
                               @Param("orderId") int orderId, 
                               @Param("status") PackageStatus status);

// get all the packages for a specified customer order
@Query("SELECT p FROM Package p WHERE p.id.customerId = :customerId AND p.id.orderId = :orderId")
List<Package> findAllByOrderIdAndCustomerId(@Param("customerId") int customerId, 
                                     @Param("orderId") int orderId);



    // clear the package list
    @Modifying
    @Transactional
    @Query("DELETE FROM Package")
    void cleanup();
}
