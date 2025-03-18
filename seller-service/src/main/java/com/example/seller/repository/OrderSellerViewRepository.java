package com.example.seller.repository;

import com.example.seller.model.OrderSellerView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface OrderSellerViewRepository extends JpaRepository<OrderSellerView, Integer> {


  @Modifying
  @Transactional
  @Query(value = "DELETE FROM seller_order_summary", nativeQuery = true)
  void clearMaterializedView();


  @Modifying
  @Transactional
  @Query(value = "INSERT INTO seller_order_summary (seller_id, customer_id, product_id, count_orders, count_items, total_amount, total_freight, total_invoice) " +
                 "SELECT o.seller_id, o.customer_id, o.product_id, COUNT(DISTINCT o.order_id), " +
                 "COUNT(o.product_id), SUM(o.total_amount), SUM(o.freight_value), SUM(o.total_invoice) " +
                 "FROM order_entries o " +
                 "WHERE o.order_status IN ('INVOICED', 'PAYMENT_PROCESSED', 'READY_FOR_SHIPMENT', 'IN_TRANSIT') " +
                 "GROUP BY o.seller_id, o.customer_id, o.product_id", nativeQuery = true)
  void populateMaterializedView();

}



