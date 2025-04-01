// package com.example.cart.repository;

// import com.example.cart.model.ProductReplica;
// import com.example.cart.model.ProductReplicaId;
// import jakarta.transaction.Transactional;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Modifying;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository;

// @Repository
// public interface JpaProductReplicaRepository extends JpaRepository<ProductReplica, ProductReplicaId>, AbstractProductReplicaRepository {

//     @Override
//     ProductReplica findByProductReplicaId(ProductReplicaId productReplicaId);

//     @Override
//     default void saveProductReplica(ProductReplica productReplica) {
//         save(productReplica);
//     }

//     @Override
//     @Modifying
//     @Transactional
//     @Query(value = "UPDATE cart.replica_product SET active = true, version = '0'", nativeQuery = true)
//     void reset();
// }