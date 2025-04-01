package com.example.product.service;

import com.example.common.events.PriceUpdate;
import com.example.product.model.Product;

public interface IProductService {

    void processCreateProduct(Product product);

    void processProductUpdate(Product product);

    void processPoisonProductUpdate(Product product);

    void processPriceUpdate(PriceUpdate priceUpdate);

    void processPoisonPriceUpdate(PriceUpdate priceUpdate);

    void cleanup();

    void reset();
}
