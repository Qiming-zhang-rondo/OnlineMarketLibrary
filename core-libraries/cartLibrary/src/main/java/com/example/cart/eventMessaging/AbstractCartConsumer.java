package com.example.cart.eventMessaging;

import com.example.common.events.ProductUpdated;
import com.example.common.events.PriceUpdate;
import com.example.cart.model.ProductReplica;
import com.example.cart.service.ICartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCartConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCartConsumer.class);

    protected final ICartService cartService;

    public AbstractCartConsumer(ICartService cartService) {
        this.cartService = cartService;
    }

    // Allow developers to implement the deserialization method of `PriceUpdate`
    protected abstract PriceUpdate deserializePriceUpdate(String payload);
    protected abstract ProductUpdated deserializeProductUpdated(String payload);

    public void handlePriceUpdate(String payload) {
        try {
            PriceUpdate priceUpdate = deserializePriceUpdate(payload);
            cartService.processPriceUpdate(priceUpdate);
        } catch (Exception e) {
            logger.error("Failed to process price update, publishing poison message: {}", e.getMessage());
            cartService.processPoisonPriceUpdate(null);
        }
    }

    public void handleProductUpdate(String payload) {
        try {
            ProductUpdated productUpdate = deserializeProductUpdated(payload);

            logger.info("Product update received at cart, seller id is {}", productUpdate.getSellerId());

            // transform to ProductReplica
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
            cartService.processPoisonProductUpdated(null);
        }
    }
}