// package com.example.cart;


// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import java.util.Optional;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

// import com.example.cart.model.Cart;
// import com.example.cart.model.CartItem;
// import com.example.cart.model.CartItemId;
// import com.example.cart.repository.CartItemRepository;
// import com.example.cart.repository.CartRepository;
// import com.example.common.entities.CartStatus;
// import com.example.common.requests.CustomerCheckout;
// import com.fasterxml.jackson.databind.ObjectMapper;

// @AutoConfigureMockMvc
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
// public class CartControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private CartRepository cartRepository;

//     @Autowired
//     private CartItemRepository cartItemRepository;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @BeforeEach
//     public void setUp() {
//         cartItemRepository.deleteAll();
//         cartRepository.deleteAll();
//     }

//     @AfterEach
//     public void tearDown() {
//         cartItemRepository.deleteAll();
//         cartRepository.deleteAll();
//     }

//     // @Test
//     // public void testAddCartItem() throws Exception {
//     //     // 模拟数据：先创建购物车
//     //     Cart cart = new Cart();
//     //     cart.setCustomerId(2);
//     //     cartRepository.save(cart);

//     //     // 创建 common.entities.CartItem
//     //     com.example.common.entities.CartItem item = new com.example.common.entities.CartItem();
//     //     item.setSellerId(1);
//     //     item.setProductId(101);
//     //     item.setProductName("Test Product");
//     //     item.setUnitPrice(10.5f);
//     //     item.setFreightValue(2.5f);
//     //     item.setQuantity(1);
//     //     item.setVoucher(0.3f);

//     //     // 发送 POST 请求
//     //     mockMvc.perform(MockMvcRequestBuilders.put("/cart/2/add")
//     //             .contentType("application/json")
//     //             .content(objectMapper.writeValueAsString(item)))
//     //             .andExpect(status().isAccepted());

//     //     // 验证数据库中是否成功保存
        
//     //     CartItemId cartItemId = new CartItemId(2, 1, 101);
//     //     Optional<CartItem> savedItem = cartItemRepository.findById(cartItemId);
//     //     assertTrue(savedItem.isPresent(), "Item should be added to cart");
//     // }

//     @Test
//     public void testSealCart() throws Exception {
//         // 模拟数据：创建一个购物车
//         Cart cart = new Cart();
//         cart.setCustomerId(2);
//         cart.setStatus(CartStatus.OPEN);
//         cartRepository.save(cart);

//         // 发送 PATCH 请求
//         mockMvc.perform(MockMvcRequestBuilders.patch("/cart/2/seal")
//                 .contentType("application/json")
//                 .content("{}")) // 空 JSON
//                 .andExpect(status().isAccepted());

//         // 验证购物车状态是否更新
//         Cart sealedCart = cartRepository.findByCustomerId(2);
//         assertTrue(sealedCart.getStatus() == CartStatus.CHECKOUT_SENT, "Cart should be sealed");
//     }

//  @Test
// public void testCheckout() throws Exception {
//     // 模拟数据：创建一个购物车
//     Cart cart = new Cart();
//     cart.setCustomerId(2);
//     cart.setStatus(CartStatus.OPEN);
//     cartRepository.save(cart);

//     // 模拟数据：创建购物车项
//     CartItem item1 = new CartItem();
//     item1.setId(new CartItemId(2, 1, 101));
//     item1.setProductName("Product A");
//     item1.setUnitPrice(50.0f);
//     item1.setFreightValue(10.0f);
//     item1.setQuantity(2);
//     item1.setCart(cart);

//     cartItemRepository.save(item1);

//     // 模拟 CustomerCheckout 请求体
//     CustomerCheckout checkout = new CustomerCheckout();
//     checkout.setCustomerId(2);
//     checkout.setInstanceId("test-instance");

//     // 发送 POST 请求
//     mockMvc.perform(MockMvcRequestBuilders.post("/cart/2/checkout")
//             .contentType("application/json")
//             .content(objectMapper.writeValueAsString(checkout))) // 发送非空 JSON 请求体
//             .andExpect(status().isAccepted());

//     // 验证购物车状态是否更新为 CHECKOUT_SENT
//     Cart updatedCart = cartRepository.findByCustomerId(2);
//     assertNotNull(updatedCart, "Cart should still exist after checkout");
//     assertEquals(CartStatus.CHECKOUT_SENT, updatedCart.getStatus(), "Cart status should be updated to CHECKOUT_SENT");
// }


  
// }
