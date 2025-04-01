package com.example.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import com.example.cart.model.CartItemId;
import com.example.cart.repository.RedisCartItemRepository;
import com.example.cart.repository.RedisCartRepository;
import com.example.cart.repository.RedisProductReplicaRepository;
import com.example.cart.service.CartServiceCore;
import com.example.common.entities.CartStatus;

@SpringBootTest
@ActiveProfiles("test")
public class CartServiceTest {

    @Autowired
    private CartServiceCore cartService;

    @Autowired
    private RedisCartRepository cartRepository;

    @Autowired
    private RedisCartItemRepository cartItemRepository;

    @Autowired
    private RedisProductReplicaRepository productReplicaRepository;

    @BeforeEach
    public void setUp() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    public void testRemoveItem() {
        Cart cart = new Cart();
        cart.setCustomerId(1);
        cartRepository.saveCart(cart);

        CartItemId cartItemId = new CartItemId(1, 456, 123);
        CartItem item = new CartItem();
        item.setId(cartItemId);
        item.setProductName("Test Product");
        item.setUnitPrice(100.0f);
        item.setFreightValue(10.0f);
        item.setQuantity(2);
        item.setVoucher(5.0f);
        item.setVersion("1.0");
        item.setCart(cart);
        cartItemRepository.saveCartItem(item);

        cartService.removeItem(1, 123, 456);
        CartItem removedItem = cartItemRepository.findById(cartItemId).orElse(null);
        assertNull(removedItem, "购物车中的商品应该已被删除");
    }

    @Test
    public void testSealCartWithCleanItems() {
        Cart cart = new Cart();
        cart.setCustomerId(1);
        cartRepository.saveCart(cart);

        CartItemId cartItemId1 = new CartItemId(1, 100, 200);
        CartItem item1 = new CartItem();
        item1.setId(cartItemId1);
        item1.setQuantity(2);
        item1.setCart(cart);
        cartItemRepository.saveCartItem(item1);

        CartItemId cartItemId2 = new CartItemId(1, 101, 201);
        CartItem item2 = new CartItem();
        item2.setId(cartItemId2);
        item2.setQuantity(1);
        item2.setCart(cart);
        cartItemRepository.saveCartItem(item2);

        cartService.seal(cart, true);

        Cart updatedCart = cartRepository.findByCustomerId(1);
        assertNotNull(updatedCart, "购物车不应该被删除");
        assertEquals(CartStatus.OPEN, updatedCart.getStatus(), "购物车状态应该为OPEN");

        Optional<CartItem> remainingItems = cartItemRepository.findById(cartItemId2);
        assertTrue(remainingItems.isEmpty(), "购物车项应该被清空");
    }

    @Test
    public void testSealCartWithoutCleanItems() {
        Cart cart = new Cart();
        cart.setCustomerId(1);
        cartRepository.saveCart(cart);

        CartItemId cartItemId1 = new CartItemId(1, 100, 200);
        CartItem item1 = new CartItem();
        item1.setId(cartItemId1);
        item1.setQuantity(2);
        item1.setCart(cart);
        cartItemRepository.saveCartItem(item1);

        CartItemId cartItemId2 = new CartItemId(1, 101, 201);
        CartItem item2 = new CartItem();
        item2.setId(cartItemId2);
        item2.setQuantity(1);
        item2.setCart(cart);
        cartItemRepository.saveCartItem(item2);

        cartService.seal(cart, false);

        Optional<CartItem> remainingItem1 = cartItemRepository.findById(cartItemId1);
        Optional<CartItem> remainingItem2 = cartItemRepository.findById(cartItemId2);

        assertTrue(remainingItem1.isPresent(), "购物车项1不应该被清空");
        assertTrue(remainingItem2.isPresent(), "购物车项2不应该被清空");
    }
}
