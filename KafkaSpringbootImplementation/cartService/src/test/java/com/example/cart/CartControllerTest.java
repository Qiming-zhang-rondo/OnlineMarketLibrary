package com.example.cart;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import com.example.cart.model.CartItemId;
import com.example.cart.repository.RedisCartItemRepository;
import com.example.cart.repository.RedisCartRepository;
import com.example.common.entities.CartStatus;
import com.example.common.requests.CustomerCheckout;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedisCartRepository cartRepository;

    @Autowired
    private RedisCartItemRepository cartItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    public void testSealCart() throws Exception {
        Cart cart = new Cart();
        cart.setCustomerId(2);
        cart.setStatus(CartStatus.OPEN);
        cartRepository.saveCart(cart);

        mockMvc.perform(MockMvcRequestBuilders.patch("/cart/2/seal")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isAccepted());

        Cart sealedCart = cartRepository.findByCustomerId(2);
        assertNotNull(sealedCart);
        assertEquals(CartStatus.OPEN, sealedCart.getStatus());
    }

    @Test
    public void testCheckout() throws Exception {
        Cart cart = new Cart();
        cart.setCustomerId(2);
        cart.setStatus(CartStatus.OPEN);
        cartRepository.saveCart(cart);

        CartItem item1 = new CartItem();
        item1.setId(new CartItemId(2, 1, 101));
        item1.setProductName("Product A");
        item1.setUnitPrice(50.0f);
        item1.setFreightValue(10.0f);
        item1.setQuantity(2);
        item1.setCart(cart);
        cartItemRepository.saveCartItem(item1);

        CustomerCheckout checkout = new CustomerCheckout();
        checkout.setCustomerId(2);
        checkout.setInstanceId("test-instance");

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/2/checkout")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(checkout)))
                .andExpect(status().isAccepted());

        Cart updatedCart = cartRepository.findByCustomerId(2);
        assertNotNull(updatedCart, "Cart should still exist after checkout");
        assertEquals(CartStatus.CHECKOUT_SENT, updatedCart.getStatus(), "Cart status should be updated to CHECKOUT_SENT");
    }
}
