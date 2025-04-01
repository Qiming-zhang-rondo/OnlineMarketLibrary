package com.example.stock.repository;

import com.example.stock.model.StockItem;
import com.example.stock.model.StockItemId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<StockItem, StockItemId> {

    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockItem s WHERE s.id.sellerId = :sellerId AND s.id.productId = :productId")
    StockItem findForUpdate(@Param("sellerId") int sellerId, @Param("productId") int productId);

    @Query("SELECT s FROM StockItem s WHERE s.id IN :ids")
    List<StockItem> findItemsByIds(@Param("ids") List<StockItemId> ids);

    // Finds an inventory item that specifies sellerId and productId
    @Query("SELECT s FROM StockItem s WHERE s.id.sellerId = :sellerId AND s.id.productId = :productId")
    StockItem findById(@Param("sellerId") int sellerId, @Param("productId") int productId);

    // get all the stovk item given the specific seller
    @Query("SELECT s FROM StockItem s WHERE s.id.sellerId = :sellerId")
    List<StockItem> findBySellerId(@Param("sellerId") int sellerId);

    // reset stock
    @Modifying
    @Query("UPDATE StockItem s SET s.active = TRUE, s.version = '0', s.qtyReserved = 0, s.qtyAvailable = :qty")
    void reset(int qty);


}
