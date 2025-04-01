package com.example.cart.controller;

import com.example.cart.kafka.CartKafkaProducer;
import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import com.example.cart.model.CartItemId;
import com.example.cart.repository.RedisCartItemRepository;
import com.example.cart.repository.RedisCartRepository;
import com.example.cart.repository.RedisProductReplicaRepository;
import com.example.cart.service.ICartService;
import com.example.common.requests.CustomerCheckout;
import com.example.common.driver.MarkStatus;
import com.example.common.entities.CartStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private RedisTemplate<String, Cart> cartRedisTemplate;

    @Autowired
    private RedisTemplate<String, CartItem> cartItemRedisTemplate;

    @Autowired
    private ICartService cartService;

    @Autowired
    private RedisCartRepository cartRepository;

    @Autowired
    private RedisCartItemRepository cartItemRepository;

    @Autowired
    private RedisProductReplicaRepository productReplicaRepository;

    private static final Logger logger = LoggerFactory.getLogger(CartKafkaProducer.class);

    @RequestMapping(value = "/{customerId}/add", method = { RequestMethod.PUT, RequestMethod.PATCH })
    public ResponseEntity<?> addItem(
            @PathVariable int customerId,
            @RequestBody com.example.common.entities.CartItem item) {
        logger.info("received add cart message: {}", item);

        if (item.getQuantity() <= 0) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Item " + item.getProductId() + " shows no positive quantity.");
        }

        String redisKey = "cart:" + customerId;

        // Step 1: try to get cart from redis
        Cart cart = (Cart) cartRedisTemplate.opsForValue().get(redisKey);

        if (cart != null && cart.getStatus() == CartStatus.CHECKOUT_SENT) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Cart for customer " + customerId + " already sent for checkout.");
        }

        if (cart == null) {

            cart = cartRepository.findByCustomerId(customerId);
            if (cart == null) {

                cart = new Cart();
                cart.setCustomerId(customerId);
                cart.setStatus(CartStatus.OPEN);
            }
        }

        // Step 2: update cart item
        CartItemId cartItemId = new CartItemId(customerId, item.getSellerId(), item.getProductId());
        boolean itemUpdated = false;

        for (CartItem existingItem : cart.getItems()) {
            if (existingItem.getId().equals(cartItemId)) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                itemUpdated = true;
                break;
            }
        }

        if (!itemUpdated) {
            // add new item
            CartItem newItem = new CartItem();
            newItem.setId(cartItemId);
            newItem.setProductName(item.getProductName());
            newItem.setUnitPrice(item.getUnitPrice());
            newItem.setFreightValue(item.getFreightValue());
            newItem.setQuantity(item.getQuantity());
            newItem.setVoucher(item.getVoucher());
            newItem.setCart(cart);

            cart.getItems().add(newItem);
        }

        // Step 3: update Redis
        cartRedisTemplate.opsForValue().set(redisKey, cart);
        logger.info("Cart updated in Redis for customer {}", customerId);

        // Step 4: save to db
        asyncUpdateCartInDatabase(cart);

        return ResponseEntity.accepted().build();
    }

    @Async
    public void asyncUpdateCartInDatabase(Cart cart) {
        try {

            cartRepository.saveCart(cart);

            cartItemRepository.saveAll(cart.getItems());
            logger.info("Cart and items updated in MySQL for customer {}", cart.getCustomerId());
        } catch (Exception e) {
            logger.error("Failed to update cart in MySQL for customer {}", cart.getCustomerId(), e);
        }
    }

    @PostMapping("/{customerId}/checkout")
    public ResponseEntity<?> notifyCheckout(@PathVariable int customerId,
            @RequestBody CustomerCheckout customerCheckout) {
        logger.info("Received checkout request for customer: {}", customerId);

        if (customerId != customerCheckout.getCustomerId()) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Customer checkout payload does not match customer ID in URL.");
        }

        
        String cartKey = "cart:" + customerId;
        Cart cart = cartRedisTemplate.opsForValue().get(cartKey);

        if (cart == null) {
            cart = cartRepository.findByCustomerId(customerCheckout.getCustomerId());
            if (cart != null) {
                cartRedisTemplate.opsForValue().set(cartKey, cart);
                logger.info("Cart found in MySQL and cached in Redis for customer: {}", customerId);
            }
        }

        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer " + customerCheckout.getCustomerId() + " cart cannot be found.");
        }

        if (cart.getStatus() == CartStatus.CHECKOUT_SENT) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Customer " + customerCheckout.getCustomerId()
                            + " cart has already been submitted for checkout.");
        }

        
        List<CartItem> items = new ArrayList<>();
        for (CartItem dbItem : cartItemRepository.findByCustomerId(customerCheckout.getCustomerId())) {
            String cartItemKey = generateCartItemRedisKey(dbItem);
            CartItem redisItem = cartItemRedisTemplate.opsForValue().get(cartItemKey);

            if (redisItem != null) {
                items.add(redisItem);
                logger.info("CartItem found in Redis: {}", redisItem);
            } else {
                items.add(dbItem); 
                cartItemRedisTemplate.opsForValue().set(cartItemKey, dbItem);
                logger.info("CartItem cached in Redis: {}", dbItem);
            }
        }

        if (items.isEmpty()) {
            logger.warn("Customer {} cart has no items to submit for checkout", customerCheckout.getCustomerId());
            cartService.processPoisonCheckout(customerCheckout, MarkStatus.NOT_ACCEPTED);
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Customer " + customerCheckout.getCustomerId()
                            + " cart has no items to be submitted for checkout.");
        }

        try {
            cartService.notifyCheckout(customerCheckout);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            cartService.processPoisonCheckout(customerCheckout, MarkStatus.ABORT);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private String generateCartItemRedisKey(CartItem item) {
        return String.format("cartItem:%d:%d:%d", item.getId().getCustomerId(), item.getId().getProductId(),
                item.getId().getSellerId());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> get(@PathVariable int customerId) {

        Cart cartEntity = cartRepository.findByCustomerId(customerId);
        if (cartEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<CartItem> items = cartItemRepository.findByCustomerId(customerId);

        com.example.common.entities.Cart responseCart = new com.example.common.entities.Cart();
        responseCart.setCustomerId(cartEntity.getCustomerId());
        responseCart.setStatus(cartEntity.getStatus());

        if (!items.isEmpty()) {
            List<com.example.common.entities.CartItem> cartItems = items.stream()
                    .map(item -> {
                        com.example.common.entities.CartItem cartItem = new com.example.common.entities.CartItem();
                        cartItem.setSellerId(item.getSellerId());
                        cartItem.setProductId(item.getProductId());
                        cartItem.setProductName(item.getProductName());
                        cartItem.setUnitPrice(item.getUnitPrice());
                        cartItem.setFreightValue(item.getFreightValue());
                        cartItem.setQuantity(item.getQuantity());
                        cartItem.setVoucher(item.getVoucher());
                        return cartItem;
                    })
                    .collect(Collectors.toList());
            responseCart.setItems(cartItems);
        } else {
            responseCart.setItems(null);
        }

        return ResponseEntity.ok(responseCart);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> delete(@PathVariable int customerId) {

        Cart cart = cartRepository.findByCustomerId(customerId);
        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        cartItemRepository.deleteByCustomerId(customerId);

        cartRepository.deleteCart(cart.getCustomerId());

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{customerId}/seal")
    public ResponseEntity<?> seal(@PathVariable int customerId) {
        logger.info("Received request to seal cart for customer: {}", customerId);

        String redisKey = "cart:" + customerId;
        Cart cart;

        try {

            cart = (Cart) cartRedisTemplate.opsForValue().get(redisKey);
            if (cart != null) {
                logger.info("Cart found in Redis for customer: {}", customerId);
            } else {

                cart = cartRepository.findByCustomerId(customerId);
                if (cart == null) {
                    logger.warn("Cart not found for customer: {}", customerId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            }

            cartService.seal(cart, true);

            return ResponseEntity.accepted().body("Cart sealed successfully for customer: " + customerId);
        } catch (Exception e) {
            logger.error("Error sealing cart for customer: {}", customerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sealing cart.");
        }
    }

    @PatchMapping("/cleanup")
    public ResponseEntity<?> cleanup() {
        cartService.cleanCart();
        return ResponseEntity.ok("Cart cleared");
    }

    @PatchMapping("/reset")
    public ResponseEntity<?> reset() {
        cartService.reset();
        return ResponseEntity.ok().build();
    }
}