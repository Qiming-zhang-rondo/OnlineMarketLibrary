package com.example.seller.repository;

import com.example.seller.model.Seller;
import com.example.seller.model.OrderEntry;
import com.example.seller.model.OrderEntryId;
import com.example.seller.dto.SellerDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {

   
    @Query("SELECT oe FROM OrderEntry oe WHERE oe.id.customerId = :customerId AND oe.id.orderId = :orderId")
    List<OrderEntry> findByCustomerIdAndOrderId(@Param("customerId") int customerId, @Param("orderId") int orderId);


   
    OrderEntry findById(OrderEntryId id);

    
    @Query("SELECT new com.example.seller.dto.SellerDashboard(v, e) " +
           "FROM OrderSellerView v LEFT JOIN OrderEntry e ON v.sellerId = e.id.sellerId " +
           "WHERE v.sellerId = :sellerId AND (e.orderStatus = 'INVOICED' OR e.orderStatus = 'READY_FOR_SHIPMENT' OR e.orderStatus = 'IN_TRANSIT' OR e.orderStatus = 'PAYMENT_PROCESSED')")
    SellerDashboard queryDashboard(@Param("sellerId") int sellerId);


    @Query("DELETE FROM Seller")
    void deleteAllSellers();

    @Query("DELETE FROM OrderEntry")
    void deleteAllOrderEntries();
}
