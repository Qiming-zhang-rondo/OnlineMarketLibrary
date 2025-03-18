package com.example.cart.repository;

import com.example.cart.model.ProductReplica;
import com.example.cart.model.ProductReplicaId;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductReplicaRepository extends JpaRepository<ProductReplica, ProductReplicaId> {


    ProductReplica findByProductReplicaId(ProductReplicaId productReplicaId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE cart.replica_product SET active = true, version = '0'", nativeQuery = true)
    void reset();
}
