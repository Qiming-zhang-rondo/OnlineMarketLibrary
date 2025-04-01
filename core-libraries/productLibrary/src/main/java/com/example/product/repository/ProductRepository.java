package com.example.product.repository;

import com.example.product.model.Product;
import com.example.product.model.ProductId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, ProductId> {

    List<Product> findByIdSellerId(int sellerId);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.status = 'ACTIVE', p.version = '0'")
    void reset();

    // Cleanup:
    @Modifying
    @Transactional
    @Query("DELETE FROM Product")
    void cleanup();


    @Override
    void deleteAll();
}
