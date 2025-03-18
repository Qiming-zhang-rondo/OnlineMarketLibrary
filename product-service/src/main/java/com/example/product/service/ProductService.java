package com.example.product.service;
import com.example.common.events.PriceUpdate;
import com.example.common.events.ProductUpdated;
import com.example.product.controller.ProductController;
import com.example.product.kafka.KafkaProductProducer;
import com.example.product.model.Product;
import com.example.product.model.ProductId;
import com.example.product.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements IProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KafkaProductProducer kafkaProductService; 

    @Override
    public void processCreateProduct(Product product) {
        productRepository.save(product);
    }

    @Override
public void processProductUpdate(Product product) {
   
    logger.info("Processing product update for productId: {}", product.getProductId());

    try {
        // get old product info from db
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + product.getId()));

        // update product information
        productRepository.save(product);
        logger.info("Product updated successfully for productId: {}", product.getProductId());

        // create ProductUpdated object
        ProductUpdated productUpdated = new ProductUpdated(
            product.getSellerId(),
            product.getProductId(),
            product.getName(),
            product.getSku(),
            product.getCategory(),
            product.getDescription(),
            product.getPrice(),
            product.getFreightValue(),
            product.getStatus(),
            product.getVersion()
        );

        // send product update info
        kafkaProductService.publishProductUpdateEvent(productUpdated);
        logger.info("Product update event sent for productId: {}", product.getProductId());

    } catch (Exception e) {
        
        logger.error("Error processing product update for productId: {}. Error: {}", product.getProductId(), e.getMessage(), e);
        throw e;  // Optionally rethrow the exception if you want to propagate it
    }
}


    @Override
    public void processPoisonProductUpdate(Product product) {
        kafkaProductService.publishPoisonProductUpdateEvent(product);
    }

    @Override
    public void processPriceUpdate(PriceUpdate priceUpdate) {
        
        Product existingProduct = productRepository
                .findById(new ProductId(priceUpdate.getSellerId(), priceUpdate.getProductId()))
                .orElseThrow(() -> new RuntimeException("Product not found"));

        
        existingProduct.setPrice(priceUpdate.getPrice());
        existingProduct.setVersion(priceUpdate.getVersion());
        productRepository.save(existingProduct);

        // send price update event
        kafkaProductService.publishPriceUpdateEvent(priceUpdate);
    }

    @Override
    public void processPoisonPriceUpdate(PriceUpdate priceUpdate) {
        kafkaProductService.publishPoisonPriceUpdateEvent(priceUpdate);
    }

    @Override
    public void cleanup() {
        productRepository.deleteAll();
    }

    @Override
    public void reset() {
       
        productRepository.reset();
    }

}
