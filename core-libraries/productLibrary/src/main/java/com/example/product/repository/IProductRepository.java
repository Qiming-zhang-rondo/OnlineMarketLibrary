package com.example.product.repository;

import com.example.product.model.Product;
import com.example.product.model.ProductId;

import java.util.List;
import java.util.Optional;

public interface IProductRepository {

    Optional<Product> findById(ProductId id);



    List<Product> findByIdSellerId(int sellerId);

    /**
     * 将所有产品状态重置为 'ACTIVE' 且版本号置为 '0'
     */
    void reset();

    /**
     * 清空所有产品记录
     */
    void cleanup();

    void deleteAll();

    void saveProduct(Product product);

    void saveAll(List<Product> products);
}
