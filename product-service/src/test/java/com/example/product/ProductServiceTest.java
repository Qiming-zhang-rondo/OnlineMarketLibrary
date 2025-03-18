// package com.example.product;

// import com.example.product.model.Product;
// import com.example.product.model.ProductId;
// import com.example.product.repository.ProductRepository;
// import com.example.product.service.ProductService;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import static org.junit.jupiter.api.Assertions.*;

// import org.junit.jupiter.api.AfterEach;

// @SpringBootTest // 启动Spring Boot上下文
// // @Transactional  // 自动回滚事务，确保测试结束后数据库不会保留脏数据
// class ProductServiceTest {

//     @Autowired
//     private ProductService productService;  // 自动注入ProductService

//     @Autowired
//     private ProductRepository productRepository;  // 自动注入Repository进行数据验证

//     @BeforeEach
//     void setup() {
//         // 每次测试之前清理数据库
//         // productRepository.deleteAll();
//     }
//     @AfterEach
//     void cleanup() {
//         // 每次测试之后清理数据库
//         // productRepository.deleteAll();
//     }

//     @Test
//     void processCreateProduct_ShouldSaveToDatabase() {
//         // Given
//         Product product = new Product();
//         ProductId productId = new ProductId(1, 101);  // 设置复合主键
//         product.setId(productId);
//         product.setName("Sample Product");
//         product.setPrice(100.00f);
//         product.setCategory("Electronics");

//         // When
//         productService.processCreateProduct(product);

//         // Then
//         Product savedProduct = productRepository.findById(productId).orElse(null);
//         assertNotNull(savedProduct);  // 验证产品是否已保存
//         assertEquals("Sample Product", savedProduct.getName());  // 验证产品名称是否一致
//         assertEquals(100.00f, savedProduct.getPrice());  // 验证价格是否一致
//         assertEquals("Electronics", savedProduct.getCategory());  // 验证分类是否一致
//     }

//     // @Test
//     // void cleanup_ShouldDeleteAllProducts() {
//     //     // Given
//     //     Product product1 = new Product();
//     //     ProductId productId1 = new ProductId(1, 101);  // 设置复合主键
//     //     product1.setId(productId1);
//     //     product1.setName("Product 1");
//     //     productService.processCreateProduct(product1);

//     //     Product product2 = new Product();
//     //     ProductId productId2 = new ProductId(2, 102);  // 设置复合主键
//     //     product2.setId(productId2);
//     //     product2.setName("Product 2");
//     //     productService.processCreateProduct(product2);

//     //     assertEquals(2, productRepository.count());  // 确保有两个产品存在

//     //     // When
//     //     productService.cleanup();

//     //     // Then
//     //     assertEquals(0, productRepository.count());  // 验证所有产品已删除
//     // }

//     @Test
//     void reset_ShouldUpdateProductStatusAndVersion() {
//         // Given
//         Product product = new Product();
//         ProductId productId = new ProductId(1, 101);  // 设置复合主键
//         product.setId(productId);
//         product.setName("Product with version");
//         product.setStatus("initial");
//         product.setVersion("test-1");
//         productService.processCreateProduct(product);

//         // When
//         productRepository.reset();
//         // Then
//         Product resetProduct = productRepository.findById(productId).orElse(null);
//         assertNotNull(resetProduct);  // 验证产品是否存在
//         assertEquals("ACTIVE", resetProduct.getStatus());  // 验证状态是否重置
//         assertEquals("0", resetProduct.getVersion());  // 验证版本号是否重置
//     }
// }
