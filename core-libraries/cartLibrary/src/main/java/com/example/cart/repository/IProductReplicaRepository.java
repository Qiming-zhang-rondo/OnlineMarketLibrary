package com.example.cart.repository;

import com.example.cart.model.ProductReplica;
import com.example.cart.model.ProductReplicaId;

public interface IProductReplicaRepository {
    ProductReplica findByProductReplicaId(ProductReplicaId productReplicaId);

    void saveProductReplica(ProductReplica productReplica);

    void reset();

    void deleteAll();
}