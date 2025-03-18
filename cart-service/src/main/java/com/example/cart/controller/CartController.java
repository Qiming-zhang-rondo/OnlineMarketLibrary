package com.example.cart.controller;

import com.example.cart.kafka.CartKafkaProducer;
import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import com.example.cart.model.CartItemId;
import com.example.cart.repository.CartItemRepository;
import com.example.cart.repository.CartRepository;
import com.example.cart.repository.ProductReplicaRepository;
import com.example.cart.service.CartService;
import com.example.common.requests.CustomerCheckout;
import com.example.common.driver.MarkStatus;
import com.example.common.entities.CartStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductReplicaRepository productReplicaRepository;

    private static final Logger logger = LoggerFactory.getLogger(CartKafkaProducer.class);

    @RequestMapping(value = "/{customerId}/add", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<?> addItem(
            @PathVariable int customerId,
            @RequestBody com.example.common.entities.CartItem item) {
        logger.info("received add cart message: {}",item);

        if (item.getQuantity() <= 0) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Item " + item.getProductId() + " shows no positive quantity.");
        }

        Cart cart = cartRepository.findByCustomerId(customerId);
        if (cart != null && cart.getStatus() == CartStatus.CHECKOUT_SENT) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Cart for customer " + customerId + " already sent for checkout.");
        }

        if (cart == null) {
            cart = new Cart();
            cart.setCustomerId(customerId);
            cart.setStatus(CartStatus.OPEN);

            cartRepository.save(cart);
        }

        CartItemId cartItemId = new CartItemId(customerId, item.getSellerId(), item.getProductId());
        CartItem existingItem = cartItemRepository.findById(cartItemId).orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setId(cartItemId);
            newItem.setProductName(item.getProductName());
            newItem.setUnitPrice(item.getUnitPrice());
            newItem.setFreightValue(item.getFreightValue());
            newItem.setQuantity(item.getQuantity());
            newItem.setVoucher(item.getVoucher());
            newItem.setCart(cart);
            // logger.info("new item: {}",newItem);

            cartItemRepository.save(newItem);
        }

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{customerId}/checkout")
    public ResponseEntity<?> notifyCheckout(@PathVariable int customerId,
            @RequestBody CustomerCheckout customerCheckout) {
        // logger.info("received customer checkout:{}", customerCheckout);
        if (customerId != customerCheckout.getCustomerId()) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Customer checkout payload does not match customer ID in URL.");
        }

        Cart cart = cartRepository.findByCustomerId(customerCheckout.getCustomerId());
        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer " + customerCheckout.getCustomerId() + " cart cannot be found.");
        }

        if (cart.getStatus() == CartStatus.CHECKOUT_SENT) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Customer " + customerCheckout.getCustomerId()
                            + " cart has already been submitted for checkout.");
        }

        List<CartItem> items = cartItemRepository.findByCustomerId(customerCheckout.getCustomerId());
        if (items == null || items.isEmpty()) {
            logger.warn("Customer {0} cart has already been submitted to checkout", customerCheckout.getCustomerId());
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


        cartRepository.delete(cart);

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{customerId}/seal")
    public ResponseEntity<?> seal(@PathVariable int customerId) {

        Cart cart = cartRepository.findByCustomerId(customerId);
        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        cartService.seal(cart, true);
        return ResponseEntity.accepted().build();

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
