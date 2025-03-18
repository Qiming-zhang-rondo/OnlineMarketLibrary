package com.example.product.kafka;

import com.example.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.common.driver.MarkStatus;
import com.example.common.driver.TransactionMark;
import com.example.common.driver.TransactionType;
import com.example.common.events.PriceUpdate;
import com.example.common.events.ProductUpdated; // 引入 ProductUpdated 类

@Service
public class KafkaProductProducer {

    @Autowired
    private KafkaTemplate<String, PriceUpdate> priceUpdateKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, ProductUpdated> productUpdatedKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, TransactionMark> transactionMarkKafkaTemplate;

    private static final String TRANSACTION_PRODUCT_UPDATE_TOPIC = "TransactionMark_UPDATE_PRODUCT";
    private static final String TRANSACTION_PRICE_UPDATE_TOPIC = "TransactionMark_PRICE_UPDATE";

    private static final String PRICE_UPDATE_TOPIC = "price-update-topic";
    private static final String PRODUCT_UPDATE_TOPIC = "product-update-topic";

    // send price update event to Kafka
    public void publishPriceUpdateEvent(PriceUpdate priceUpdate) {
        priceUpdateKafkaTemplate.send(PRICE_UPDATE_TOPIC, priceUpdate);
    }

    // send product update event to Kafka
    public void publishProductUpdateEvent(ProductUpdated productUpdated) {
        productUpdatedKafkaTemplate.send(PRODUCT_UPDATE_TOPIC, productUpdated);
    }
    

    public void publishPoisonPriceUpdateEvent(PriceUpdate priceUpdate) {
        TransactionMark transactionMark = new TransactionMark(
                priceUpdate.getInstanceId(),
                TransactionType.PRICE_UPDATE,
                priceUpdate.getSellerId(),
                MarkStatus.ERROR,
                "product");

        transactionMarkKafkaTemplate.send(TRANSACTION_PRICE_UPDATE_TOPIC, transactionMark);
    }

    // handle poison product updated event
    public void publishPoisonProductUpdateEvent(Product product) {
        TransactionMark transactionMark = new TransactionMark(
                product.getVersion(),
                TransactionType.UPDATE_PRODUCT,
                product.getSellerId(),
                MarkStatus.ERROR,
                "product");

        transactionMarkKafkaTemplate.send(TRANSACTION_PRODUCT_UPDATE_TOPIC, transactionMark);
    }
}
