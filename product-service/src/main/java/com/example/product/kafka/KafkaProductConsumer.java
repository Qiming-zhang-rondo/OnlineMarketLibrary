package com.example.product.kafka;

import com.example.common.events.ProductUpdated; // 引入 ProductUpdated 类
import com.example.product.model.Product;
import com.example.product.model.ProductId;
import com.example.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaProductConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProductConsumer.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KafkaProductProducer kafkaProductProducer;

    @KafkaListener(topics = "product-request-topic", groupId = "product-group")
    public void consumeProductRequest(ProductUpdated productRequest) {
        // logger.info("Product request received at product service.");
        // logger.info("Seller ID = {}", productRequest.getSellerId());
        // logger.info("Product ID = {}", productRequest.getProductId());

    
        // ProductId productId = new ProductId(productRequest.getSellerId(), productRequest.getProductId());
        // Product product = productRepository.findById(productId).orElse(null);

        // if (product != null) {
        //     kafkaProductProducer.publishProductUpdateEvent(product);  
        //     logger.info("Product update event sent.");
        // } else {
        //     logger.warn("Product with specified ID not found.");
        // }
    }
}
