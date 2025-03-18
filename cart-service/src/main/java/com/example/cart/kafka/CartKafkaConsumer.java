package com.example.cart.kafka;

import com.example.common.events.ProductUpdated;
import com.example.common.events.PriceUpdate;
import com.example.common.driver.TransactionMark;
import com.example.common.driver.MarkStatus;
import com.example.common.driver.TransactionType;
import com.example.cart.model.ProductReplica;
import com.example.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CartKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CartKafkaConsumer.class);

    @Autowired
    private CartService cartService;  


    @KafkaListener(topics = "price-update-topic", groupId = "cart-group")
    public void handlePriceUpdate(PriceUpdate priceUpdate) {
        try {
            cartService.processPriceUpdate(priceUpdate); 
        } catch (Exception e) {
            logger.error("Failed to process price update, publishing poison message: {}", e.getMessage());
        
            cartService.processPoisonPriceUpdate(priceUpdate);  
        }
    }


    @KafkaListener(topics = "product-update-topic", groupId = "cart-group")
    public void handleProductUpdate(ProductUpdated productUpdate) {
        try {
            logger.info("Product update received at cart, seller id is {}", productUpdate.getSellerId());
            ProductReplica productReplica = new ProductReplica();
            productReplica.setSellerId(productUpdate.getSellerId());
            productReplica.setProductId(productUpdate.getProductId());
            productReplica.setName(productUpdate.getName());
            productReplica.setPrice(productUpdate.getPrice());
            productReplica.setVersion(productUpdate.getVersion());
            productReplica.setActive(true);

            cartService.processProductUpdated(productReplica);  
        } catch (Exception e) {
            logger.error("Failed to process product update, publishing poison message: {}", e.getMessage());
        
            cartService.processPoisonProductUpdated(productUpdate);  
        }
    }
}
