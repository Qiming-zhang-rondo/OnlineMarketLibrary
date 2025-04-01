package com.example.seller.repository;

import com.example.common.entities.OrderStatus;
import com.example.seller.model.OrderEntry;
import com.example.seller.model.OrderEntryId;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEntryRepository extends JpaRepository<OrderEntry, OrderEntryId> {

   
    @Query("SELECT oe FROM OrderEntry oe WHERE oe.id.customerId = :customerId AND oe.id.orderId = :orderId")
    List<OrderEntry> findByCustomerIdAndOrderId(@Param("customerId") int customerId, @Param("orderId") int orderId);

    @Query("SELECT e FROM OrderEntry e WHERE e.id.sellerId = :sellerId AND " +
            "(e.orderStatus = 'INVOICED' OR e.orderStatus = 'READY_FOR_SHIPMENT' OR e.orderStatus = 'IN_TRANSIT' OR e.orderStatus = 'PAYMENT_PROCESSED')")
    List<OrderEntry> findAllBySellerId(@Param("sellerId") int sellerId);

    @Query("SELECT oe.id.sellerId, " +
            "COUNT(DISTINCT oe.id.orderId) AS countOrders, " +
            "COUNT(oe.id.productId) AS countItems, " +
            "SUM(oe.totalAmount) AS totalAmount, " +
            "SUM(oe.freightValue) AS totalFreight, " +
            "SUM(oe.totalInvoice) AS totalInvoice " +
            "FROM OrderEntry oe " +
            "WHERE oe.orderStatus IN :statuses " +
            "GROUP BY oe.id.sellerId")
    List<Object[]> findAllSellerAggregates(@Param("statuses") List<OrderStatus> statuses);

}
