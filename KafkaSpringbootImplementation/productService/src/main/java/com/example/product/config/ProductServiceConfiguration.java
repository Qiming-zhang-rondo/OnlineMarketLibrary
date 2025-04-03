package com.example.product.config;

import com.example.product.repository.RedisProductRepository;
import com.example.product.service.*;
import com.example.product.kafka.ProductKafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ProductServiceConfiguration {
    @Bean

    public IProductService productService(RedisProductRepository productRepository
        , ProductKafkaProducer productKafkaProducer) {
        return new ProductServiceCore(productRepository, productKafkaProducer);
    }

}
