package com.example.cart.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.common.driver.MarkStatus;
import com.example.common.driver.TransactionMark;
import com.example.common.driver.TransactionType;
import com.example.common.entities.CartStatus;
import com.example.common.events.PriceUpdate;
import com.example.common.events.ProductUpdated;
import com.example.common.events.ReserveStock;
import com.example.common.requests.CustomerCheckout;
import com.example.cart.kafka.CartKafkaProducer;
import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import com.example.cart.model.CartItemId;
import com.example.cart.model.ProductReplica;
import com.example.cart.model.ProductReplicaId;
import com.example.cart.repository.CartItemRepository;
import com.example.cart.repository.CartRepository;
import com.example.cart.repository.ProductReplicaRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CartService implements ICartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductReplicaRepository productReplicaRepository;

    @Autowired
    private CartKafkaProducer cartKafkaProducer;

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    
    

    // @Transactional
    @Override
    public void removeItem(int customerId, int productId, int sellerId) {
        Cart cart = cartRepository.findByCustomerId(customerId);
        if (cart != null) {
            CartItemId itemId = new CartItemId(customerId, sellerId, productId);
            Optional<CartItem> item = cartItemRepository.findById(itemId);

            if (item.isPresent()) {
                cartItemRepository.delete(item.get());
                // cartItemRepository.flush();
            }
        }
    }

    @Transactional
    @Override
    public void seal(Cart cart, boolean cleanItems) {
        cart.setStatus(CartStatus.OPEN);
        if (cleanItems) {
           
            cart.getItems().clear();
            cartRepository.save(cart);
            // System.out.println("Cart items cleared.");
        }
        cart.setUpdatedAt(LocalDateTime.now());

    }

    @Override
    @Transactional
    public void notifyCheckout(CustomerCheckout customerCheckout) {
        try {
           
            Cart cart = cartRepository.findByCustomerId(customerCheckout.getCustomerId());
            if (cart == null) {
                throw new Exception("Cart " + customerCheckout.getCustomerId() + " not found");
            }

            if (cart.getStatus() == CartStatus.CHECKOUT_SENT) {
                throw new Exception(
                        "Cart " + customerCheckout.getCustomerId() + " has already been submitted for checkout");
            }

            
            List<CartItem> items = cartItemRepository.findByCustomerId(customerCheckout.getCustomerId());
            if (items.isEmpty()) {
                throw new Exception("Cart " + customerCheckout.getCustomerId() + " has no items");
            }

    
            cart.setStatus(CartStatus.CHECKOUT_SENT);
            cartRepository.save(cart);

          
            List<com.example.common.entities.CartItem> cartItems = items.stream()
                    .map(i -> {
                        com.example.common.entities.CartItem cartItem = new com.example.common.entities.CartItem();
                        cartItem.setSellerId(i.getSellerId());
                        cartItem.setProductId(i.getProductId());
                        cartItem.setProductName(i.getProductName() == null ? "" : i.getProductName());
                        cartItem.setUnitPrice(i.getUnitPrice());
                        cartItem.setFreightValue(i.getFreightValue());
                        cartItem.setQuantity(i.getQuantity());
                        cartItem.setVersion(i.getVersion());
                        cartItem.setVoucher(i.getVoucher());
                        return cartItem;
                    })
                    .collect(Collectors.toList());

            // Seal the cart
            // seal(cart, true);

            LocalDateTime timestamp = LocalDateTime.now();

         
            ReserveStock checkout = new ReserveStock(
                    timestamp,
                    customerCheckout,
                    cartItems,
                    customerCheckout.getInstanceId());

            cartKafkaProducer.sendReserveStock(checkout);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process checkout: " + e.getMessage(), e);
        }
    }

    @Override
    public void cleanCart() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productReplicaRepository.deleteAll();

    }

    @Override
    public Cart getCart(int customerId) {
      
        return cartRepository.findByCustomerId(customerId);
    }

    @Transactional
    @Override
    public void processProductUpdated(ProductReplica productUpdated) {
        ProductReplica existingProduct = productReplicaRepository.findByProductReplicaId(
                new ProductReplicaId(productUpdated.getSellerId(), productUpdated.getProductId()));

        // If no existing product is found, a new ProductReplica is created
        if (existingProduct == null) {
            logger.info("existing product is null");
            existingProduct = new ProductReplica();
            existingProduct.setSellerId(productUpdated.getSellerId());
            existingProduct.setProductId(productUpdated.getProductId());
            existingProduct.setCreatedAt(LocalDateTime.now());
        }

        // update other fields
        logger.info("existing product is not null with seller id is {}", existingProduct.getSellerId());
        existingProduct.setName(productUpdated.getName());
        // existingProduct.setName("");
        existingProduct.setPrice(productUpdated.getPrice());
        existingProduct.setVersion(productUpdated.getVersion());
        existingProduct.setActive(productUpdated.isActive());
        existingProduct.setUpdatedAt(LocalDateTime.now()); 

        // save updated ProductReplica
        productReplicaRepository.save(existingProduct);

    }

    @Override
    @Transactional
    public void processPriceUpdate(PriceUpdate priceUpdate) {
        ProductReplica product = productReplicaRepository
                .findByProductReplicaId(new ProductReplicaId(priceUpdate.getSellerId(), priceUpdate.getProductId()));
    
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product not found: " + priceUpdate.getSellerId() + "-" + priceUpdate.getProductId());
        }
    
        // if (!product.getVersion().equals(priceUpdate.getVersion())) {
        //     throw new IllegalStateException("Version does not match: " + priceUpdate.getVersion());
        // }
    
        product.setPrice(priceUpdate.getPrice());
        productReplicaRepository.save(product);
    
        List<CartItem> cartItems = cartItemRepository.findBySellerIdAndProductId(priceUpdate.getSellerId(),
                priceUpdate.getProductId());
        for (CartItem cartItem : cartItems) {
            float oldPrice = cartItem.getUnitPrice(); 
            cartItem.setUnitPrice(priceUpdate.getPrice()); 
            cartItem.setVoucher(cartItem.getVoucher() + (oldPrice - priceUpdate.getPrice())); 
        }
        cartItemRepository.saveAll(cartItems);
    
        TransactionMark transactionMark = new TransactionMark(
                priceUpdate.getInstanceId(),
                TransactionType.PRICE_UPDATE,
                priceUpdate.getSellerId(),
                MarkStatus.SUCCESS,
                "cart");
    
        cartKafkaProducer.sendPriceUpdateTransactionMark(transactionMark);
    }
    

    @Override
    public void reset() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        ;
        productReplicaRepository.reset();
    }

    @Override
    public void processPoisonProductUpdated(ProductUpdated productUpdated) {
        TransactionMark transactionMark = new TransactionMark(
                productUpdated.getVersion(),
                TransactionType.UPDATE_PRODUCT,
                productUpdated.getSellerId(),
                MarkStatus.ABORT,
                "cart");
        cartKafkaProducer.sendPoisonProductUpdated(transactionMark);
    }

    @Override
    public void processPoisonPriceUpdate(PriceUpdate priceUpdated) {
        TransactionMark transactionMark = new TransactionMark(
                priceUpdated.getInstanceId(),
                TransactionType.PRICE_UPDATE,
                priceUpdated.getSellerId(),
                MarkStatus.ABORT,
                "cart");
        cartKafkaProducer.sendPoisonPriceUpdate(transactionMark);
    }

    public void processPoisonCheckout(CustomerCheckout customerCheckout, MarkStatus status) {
        TransactionMark transactionMark = new TransactionMark(
                customerCheckout.getInstanceId(),
                TransactionType.CUSTOMER_SESSION,
                customerCheckout.getCustomerId(),
                status,
                "cart");

        cartKafkaProducer.sendPoisonCheckout(transactionMark);
    }

}
