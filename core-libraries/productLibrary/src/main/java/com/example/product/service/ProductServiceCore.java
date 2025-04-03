package com.example.product.service;

import com.example.common.events.PriceUpdate;
import com.example.common.events.ProductUpdated;
import com.example.product.model.Product;
import com.example.product.model.ProductId;
import com.example.product.repository.IProductRepository;
//import com.example.product.kafka.IKafkaProductProducer;
import com.example.common.messaging.IEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductServiceCore implements IProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceCore.class);

    private final IProductRepository productRepository;
    private final IEventPublisher eventPublisher;

    public ProductServiceCore(IProductRepository productRepository, IEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void processCreateProduct(Product product) {
        productRepository.saveProduct(product);
    }

    @Override
    public void processProductUpdate(Product product) {
        logger.info("Processing product update for productId: {}", product.getProductId());
        try {
            // 从仓库中获取旧的产品信息
            Product existingProduct = productRepository.findById(product.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + product.getId()));

            // 更新产品信息
            productRepository.saveProduct(product);
            logger.info("Product updated successfully for productId: {}", product.getProductId());

            // 构造 ProductUpdated 事件对象
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

            // 发送产品更新事件
            eventPublisher.publishEvent("product-update-topic", productUpdated);
            logger.info("Product update event sent for productId: {}", product.getProductId());
        } catch (Exception e) {
            logger.error("Error processing product update for productId: {}. Error: {}",
                    product.getProductId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    //???
    public void processPoisonProductUpdate(Product product) {
        eventPublisher.publishEvent("TransactionMark_UPDATE_PRODUCT",product);
    }

    @Override
    public void processPriceUpdate(PriceUpdate priceUpdate) {
        // 根据 sellerId 和 productId 构造 ProductId 查询产品
        Product existingProduct = productRepository
                .findById(new ProductId(priceUpdate.getSellerId(), priceUpdate.getProductId()))
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 更新价格和版本
        existingProduct.setPrice(priceUpdate.getPrice());
        existingProduct.setVersion(priceUpdate.getVersion());
        productRepository.saveProduct(existingProduct);

        // 发送价格更新事件
        eventPublisher.publishEvent("price-update-topic",priceUpdate);
    }

    @Override
    public void processPoisonPriceUpdate(PriceUpdate priceUpdate) {
        eventPublisher.publishEvent("TransactionMark_PRICE_UPDATE",priceUpdate);
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

