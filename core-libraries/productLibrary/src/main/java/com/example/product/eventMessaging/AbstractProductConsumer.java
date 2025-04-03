package com.example.product.eventMessaging;

import com.example.common.events.ProductUpdated;
import com.example.common.messaging.IEventPublisher;
import com.example.product.model.Product;
import com.example.product.model.ProductId;
import com.example.product.repository.IProductRepository;
//import com.example.product.kafka.IKafkaProductProducer;
import com.example.product.service.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.common.messaging.IEventPublisher;

public abstract class AbstractProductConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractProductConsumer.class);

    protected final IProductRepository productRepository;
    protected final IEventPublisher eventPublisher;

//    public AbstractProductConsumer(IProductService productService, IProductRepository productRepository, IEventPublisher eventPublisher) {
//        this.productService = productService;
//        this.productRepository = productRepository;
//        this.eventPublisher = eventPublisher;
//    }
public AbstractProductConsumer(IProductRepository productRepository, IEventPublisher eventPublisher) {
    this.productRepository = productRepository;
    this.eventPublisher = eventPublisher;
}

    // 开发者需要实现 payload 到 ProductUpdated 对象的反序列化方法 ?
    //是payload吗 JSON关联 productRequest
    protected abstract ProductUpdated deserializeProductUpdated(String productRequest);

    public void handleProductRequest(String productRequest) {
//        try {
//            ProductUpdated productRequest = deserializeProductUpdated(productRequest);
//            logger.info("Product request received. Seller ID = {}, Product ID = {}",
//                    productRequest.getSellerId(), productRequest.getProductId());
//
//            ProductId productId = new ProductId(productRequest.getSellerId(), productRequest.getProductId());
//            Product product = productRepository.findById(productId).orElse(null);
//
//            if (product != null) {
//                kafkaProductProducer.publishProductUpdateEvent(product);
//                logger.info("Product update event sent.");
//            } else {
//                logger.warn("Product with specified ID not found: {}", productId);
//            }
//        } catch (Exception e) {
//            logger.error("Error processing product request: {}", e.getMessage(), e);
//        }
    }
}

