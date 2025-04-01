package com.example.cart.service;

import com.example.cart.model.*;
import com.example.cart.repository.*;
import com.example.common.driver.*;
import com.example.common.entities.CartStatus;
import com.example.common.events.*;
import com.example.common.messaging.IEventPublisher;
import com.example.common.requests.CustomerCheckout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CartServiceCore implements ICartService {
    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final IProductReplicaRepository productReplicaRepository;
    private final IEventPublisher eventPublisher;

    private static final Logger logger = LoggerFactory.getLogger(CartServiceCore.class);

    public CartServiceCore(ICartRepository cartRepository,
                           ICartItemRepository cartItemRepository,
                           IProductReplicaRepository productReplicaRepository,
                           IEventPublisher eventPublisher) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productReplicaRepository = productReplicaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Cart getCart(int customerId) {
        return cartRepository.findByCustomerId(customerId);
    }

    @Override
    public void removeItem(int customerId, int productId, int sellerId) {
        Cart cart = cartRepository.findByCustomerId(customerId);
        if (cart != null) {
            CartItemId itemId = new CartItemId(customerId, sellerId, productId);
            Optional<CartItem> item = cartItemRepository.findById(itemId);
            if (item.isPresent()) {
                cartItemRepository.delete(item.get());
            }
        }
    }

    @Override
    public void seal(Cart cart, boolean cleanItems) {
        cart.setStatus(CartStatus.OPEN);
        if (cleanItems) {
            cartItemRepository.deleteByCustomerId(cart.getCustomerId());
        }
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.saveCart(cart);
    }

    @Override
    public void notifyCheckout(CustomerCheckout customerCheckout) {
        Cart cart = cartRepository.findByCustomerId(customerCheckout.getCustomerId());
        if (cart == null) {
            throw new RuntimeException("Cart not found for customer: " + customerCheckout.getCustomerId());
        }

        if (cart.getStatus() == CartStatus.CHECKOUT_SENT) {
            throw new RuntimeException("Cart already submitted for checkout: " + customerCheckout.getCustomerId());
        }

        List<CartItem> items = cartItemRepository.findByCustomerId(customerCheckout.getCustomerId());
        if (items.isEmpty()) {
            throw new RuntimeException("Cart has no items: " + customerCheckout.getCustomerId());
        }

        cart.setStatus(CartStatus.CHECKOUT_SENT);
        cartRepository.saveCart(cart);

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

        LocalDateTime timestamp = LocalDateTime.now();
        ReserveStock checkout = new ReserveStock(timestamp, customerCheckout, cartItems, customerCheckout.getInstanceId());
        eventPublisher.publishEvent("reserve-stock-topic", checkout);
    }

    @Override
    public void cleanCart() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productReplicaRepository.deleteAll();
    }

    @Override
    public void processProductUpdated(ProductReplica productUpdated) {
        ProductReplica existingProduct = productReplicaRepository.findByProductReplicaId(
            new ProductReplicaId(productUpdated.getSellerId(), productUpdated.getProductId()));

        if (existingProduct == null) {
            logger.info("existing product is null");
            existingProduct = new ProductReplica();
            existingProduct.setSellerId(productUpdated.getSellerId());
            existingProduct.setProductId(productUpdated.getProductId());
            existingProduct.setCreatedAt(LocalDateTime.now());
        }

        logger.info("existing product is not null with seller id is {}", existingProduct.getSellerId());
        existingProduct.setName(productUpdated.getName());
        existingProduct.setPrice(productUpdated.getPrice());
        existingProduct.setVersion(productUpdated.getVersion());
        existingProduct.setActive(productUpdated.isActive());
        existingProduct.setUpdatedAt(LocalDateTime.now());

        productReplicaRepository.saveProductReplica(existingProduct);
    }

    @Override
    public void processPriceUpdate(PriceUpdate priceUpdate) {
        ProductReplica product = productReplicaRepository.findByProductReplicaId(
            new ProductReplicaId(priceUpdate.getSellerId(), priceUpdate.getProductId()));

        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + priceUpdate.getSellerId() + "-" + priceUpdate.getProductId());
        }

        product.setPrice(priceUpdate.getPrice());
        productReplicaRepository.saveProductReplica(product);

        List<CartItem> cartItems = cartItemRepository.findBySellerIdAndProductId(
            priceUpdate.getSellerId(), priceUpdate.getProductId());

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

        eventPublisher.publishEvent("TransactionMark_PRICE_UPDATE", transactionMark);
    }

    @Override
    public void reset() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
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
        eventPublisher.publishEvent("TransactionMark_UPDATE_PRODUCT", transactionMark);
    }

    @Override
    public void processPoisonPriceUpdate(PriceUpdate priceUpdated) {
        TransactionMark transactionMark = new TransactionMark(
            priceUpdated.getInstanceId(),
            TransactionType.PRICE_UPDATE,
            priceUpdated.getSellerId(),
            MarkStatus.ABORT,
            "cart");
        eventPublisher.publishEvent("TransactionMark_PRICE_UPDATE", transactionMark);
    }

    @Override
    public void processPoisonCheckout(CustomerCheckout customerCheckout, MarkStatus status) {
        TransactionMark transactionMark = new TransactionMark(
            customerCheckout.getInstanceId(),
            TransactionType.CUSTOMER_SESSION,
            customerCheckout.getCustomerId(),
            status,
            "cart");

        eventPublisher.publishEvent("TransactionMark_CUSTOMER_SESSION", transactionMark);
    }
}