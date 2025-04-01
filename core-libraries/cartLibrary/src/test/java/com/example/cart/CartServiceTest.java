// package com.example.cart;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;

// import com.example.cart.model.Cart;
// import com.example.cart.model.CartItem;
// import com.example.cart.model.CartItemId;
// import com.example.cart.model.ProductReplica;
// import com.example.cart.model.ProductReplicaId;
// import com.example.cart.repository.CartItemRepository;
// import com.example.cart.repository.CartRepository;
// import com.example.cart.repository.ProductReplicaRepository;
// import com.example.cart.service.CartService;
// import com.example.common.entities.CartStatus;

// @SpringBootTest(classes = CartApplication.class)
// @ActiveProfiles("test")
// public class CartServiceTest {

//     @Autowired
//     private CartService cartService;

//     @Autowired
//     private CartRepository cartRepository;

//     @Autowired
//     private CartItemRepository cartItemRepository;

//     @Autowired
//     private ProductReplicaRepository productReplicaRepository;

//     @BeforeEach
//     public void setUp() {
//         // 每次测试前清空数据库
//         cartItemRepository.deleteAll();
//         cartRepository.deleteAll();
//         // productReplicaRepository.deleteAll();

//     }

//     // @AfterEach
//     // public void tearDown() {
//     // // 每次测试后清理数据
//     // // cartItemRepository.deleteAll();
//     // // cartRepository.deleteAll();
//     // }

//     // @Test
//     // public void testAddCartItem() {
//     //     // 模拟数据
//     //     Cart cart = new Cart();
//     //     cart.setCustomerId(2); // 设置客户ID

//     //     // 首先保存 Cart
//     //     cartRepository.save(cart);

//     //     CartItem item = new CartItem();
//     //     CartItemId itemId = new CartItemId(cart.getCustomerId(), 1, 101); // 设置复合主键
//     //     item.setId(itemId);
//     //     item.setProductName("Test Product");
//     //     item.setUnitPrice(100.0f);
//     //     item.setFreightValue(10.0f);
//     //     item.setQuantity(2);
//     //     item.setVoucher(5.0f);
//     //     item.setVersion("1.0");
//     //     item.setCart(cart); // 关联购物车

//     //     // 添加商品
//     //     cartService.addItem(cart.getCustomerId(), item);

//     //     // 查找保存后的商品
//     //     Optional<CartItem> savedItem = cartItemRepository.findById(itemId);

//     //     // 验证是否保存成功
//     //     assertEquals(savedItem.get().getFreightValue(), item.getFreightValue());
//     //     ProductReplica existingProduct = productReplicaRepository
//     //             .findByProductReplicaId(new ProductReplicaId(itemId.getSellerId(), itemId.getProductId()));
//     //     assertEquals(1, existingProduct.getSellerId());
//     // }

//     @Test
//     public void testRemoveItem() {
//         // 准备测试数据
//         Cart cart = new Cart();
//         cart.setCustomerId(1);
//         cartRepository.save(cart);

//         // 创建CartItem并设置相关数据
//         CartItemId cartItemId = new CartItemId(1, 456, 123);
//         CartItem item = new CartItem();
//         item.setId(cartItemId);
//         item.setProductName("Test Product");
//         item.setUnitPrice(100.0f);
//         item.setFreightValue(10.0f);
//         item.setQuantity(2);
//         item.setVoucher(5.0f);
//         item.setVersion("1.0");
//         item.setCart(cart); // 关联购物车
//         cartItemRepository.save(item);

//         // 调用 removeItem 方法删除购物车中的商品
//         cartService.removeItem(1, 123, 456);

//         // 验证商品是否从购物车中删除
//         CartItem removedItem = cartItemRepository.findById(cartItemId).orElse(null);
//         assertNull(removedItem, "购物车中的商品应该已被删除");
//     }



//     @Test
//     public void testSealCartWithCleanItems() {
//         // 模拟购物车及其购物车项
//         Cart cart = new Cart();
//         cart.setCustomerId(1);
//         cartRepository.save(cart);

//         // 创建购物车项及其复合主键
//         CartItemId cartItemId1 = new CartItemId(1, 100, 200);
//         CartItem item1 = new CartItem();
//         item1.setId(cartItemId1);
//         item1.setQuantity(2);
//         item1.setCart(cart); // 设置关联的购物车
//         cartItemRepository.save(item1);

//         CartItemId cartItemId2 = new CartItemId(1, 101, 201);
//         CartItem item2 = new CartItem();
//         item2.setId(cartItemId2);
//         item2.setQuantity(1);
//         item2.setCart(cart); // 设置关联的购物车
//         cartItemRepository.save(item2);

//         // 调用seal方法，清空购物车项
//         cartService.seal(cart, true);

//         // 验证购物车状态
//         Cart updatedCart = cartRepository.findByCustomerId(1);
//         assertNotNull(updatedCart, "购物车不应该被删除");
//         assertEquals(CartStatus.CHECKOUT_SENT, updatedCart.getStatus(), "购物车状态应该为OPEN");

//         // 验证购物车项是否已被清空
//         Optional<CartItem> remainingItems = cartItemRepository.findById(cartItemId2);
//         assertTrue(remainingItems.isEmpty(), "购物车项应该被清空");
//     }

//     @Test
//     public void testSealCartWithoutCleanItems() {
//         // 模拟购物车及其购物车项
//         Cart cart = new Cart();
//         cart.setCustomerId(1);
//         cartRepository.save(cart);

//         // 创建购物车项及其复合主键
//         CartItemId cartItemId1 = new CartItemId(1, 100, 200);
//         CartItem item1 = new CartItem();
//         item1.setId(cartItemId1);
//         item1.setQuantity(2);
//         item1.setCart(cart); // 设置关联的购物车
//         cartItemRepository.save(item1);

//         CartItemId cartItemId2 = new CartItemId(1, 101, 201);
//         CartItem item2 = new CartItem();
//         item2.setId(cartItemId2);
//         item2.setQuantity(1);
//         item2.setCart(cart); // 设置关联的购物车
//         cartItemRepository.save(item2);

//         // Optional<CartItem> remainingItem1 = cartItemRepository.findById(cartItemId1);
//         // assertTrue(remainingItem1.isPresent(), "购物车项1不应该被清空");
//         // 调用seal方法，不清空购物车项
//         cartService.seal(cart, false);

//         // 验证购物车项是否仍然存在
//         Optional<CartItem> remainingItem1 = cartItemRepository.findById(cartItemId1);
//         Optional<CartItem> remainingItem2 = cartItemRepository.findById(cartItemId2);

//         // 确保购物车项没有被删除
//         assertTrue(remainingItem1.isPresent(), "购物车项1不应该被清空");
//         assertTrue(remainingItem2.isPresent(), "购物车项2不应该被清空");
//     }

// }
