// package com.example.product;

// import com.example.common.events.PriceUpdate;
// import com.example.product.model.Product;
// import com.example.product.model.ProductId;
// import com.example.product.repository.ProductRepository;
// import com.example.product.service.ProductService;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.http.MediaType;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

// @SpringBootTest // 启动完整的 Spring Boot 应用程序上下文
// @AutoConfigureMockMvc // 自动配置 MockMvc

// public class ProductControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ProductRepository productRepository; // 使用实际的 Repository

//     @Autowired
//     private ProductService productService; // 使用实际的 Service

//     private Product product;

//     // @AfterEach
//     // void cleanup() {
//     //     // 每次测试之前清理数据库
//     //     productRepository.deleteAll();
//     // }

//     @BeforeEach
//     void setup() {
//         // 初始化测试用的 Product 实体
//         product = new Product();
//         ProductId productId = new ProductId(1, 101);
//         product.setId(productId);
//         product.setName("Test Product");
//         product.setStatus("initial");
//         product.setVersion("0");
//         productRepository.save(product); // 在数据库中插入测试数据
//     }

//     @Test
//     void testGetBySellerIdAndProductId() throws Exception {
//         // 模拟 HTTP GET 请求，获取产品信息
//         mockMvc.perform(get("/product/1/101")
//                 .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk()) // 期望状态码 200 OK
//                 .andExpect(jsonPath("$.name").value("Test Product")) // 验证返回的 JSON 数据
//                 .andExpect(jsonPath("$.status").value("initial"))
//                 .andDo(print()); // 打印请求和响应内容
//     }

//     @Test
//     void testAddProduct() throws Exception {
//         // 模拟 HTTP POST 请求，添加产品
//         mockMvc.perform(post("/product/")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(
//                         "{\"seller_id\": 2, \"product_id\": 202, \"name\": \"New Product\", \"sku\": \"SKU-202\", \"category\": \"Electronics\", \"description\": \"A new product\", \"price\": 49.99, \"freight_value\": 5.0, \"status\": \"initial\", \"version\": \"test-1\"}")
//                 .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isCreated()) // 期望状态码 201 Created
//                 .andDo(print()); // 打印请求和响应内容

//         // 验证数据库是否插入了新的产品
//         Product addedProduct = productRepository.findById(new ProductId(2, 202)).orElse(null);
//         assertNotNull(addedProduct);
//         assertEquals("New Product", addedProduct.getName());
//         assertEquals("SKU-202", addedProduct.getSku());
//         assertEquals("Electronics", addedProduct.getCategory());
//     }

//     @Test
//     void testReset() throws Exception {
//         // 模拟 HTTP PATCH 请求，重置产品状态
//         mockMvc.perform(patch("/product/reset")
//                 .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk()) // 期望状态码 200 OK
//                 .andDo(print()); // 打印请求和响应内容

//         // 验证产品状态是否已更新
//         Product resetProduct = productRepository.findById(new ProductId(1, 101)).orElse(null);
//         assertNotNull(resetProduct);
//         assertEquals("ACTIVE", resetProduct.getStatus());
//         assertEquals("0", resetProduct.getVersion());
//     }

//     @Test
//     void testUpdateProduct() throws Exception {
//         // 创建一个初始的产品并保存到数据库
//         Product product = new Product();
//         product.setId(new ProductId(51, 2));
//         product.setName("Original Product");
//         product.setStatus("pending");
//         product.setVersion("1");
//         product.setPrice(100.0f); // 假设产品价格
//         product.setFreightValue(5.0f); // 假设运费
//         product.setSku("HDAU66QD3D577D8I"); // 假设 SKU
//         product.setCategory("Tools"); // 假设产品类别
//         product.setDescription("Original description"); // 假设描述
//         productRepository.save(product); // 保存到数据库

//         // 模拟 HTTP PUT 请求，更新产品
//         mockMvc.perform(put("/product/")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(
//                         "{\"seller_id\": 51, \"product_id\": 2, \"name\": \"Refined Frozen Cheese\", \"sku\": \"HDAU66QD3D577D8I\", \"category\": \"Tools\", \"description\": \"New range of formal shirts are designed keeping you in mind. With fits and styling that will make you stand apart\", \"price\": 88.14, \"freight_value\": 6.82, \"status\": \"approved\", \"version\": \"99\"}")
//                 .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk()) // 期望状态码 200 OK
//                 .andDo(print()); // 打印请求和响应内容

//         // 验证数据库中的产品是否已更新
//         Product updatedProduct = productRepository.findById(new ProductId(51, 2)).orElse(null);
//         assertNotNull(updatedProduct);
//         assertEquals("Refined Frozen Cheese", updatedProduct.getName()); // 验证产品名称是否更新
//         assertEquals("approved", updatedProduct.getStatus()); // 验证状态是否更新
//         assertEquals("99", updatedProduct.getVersion()); // 验证版本号是否更新
//         assertEquals(88.14f, updatedProduct.getPrice(), 0.01); // 验证价格是否更新
//         assertEquals(6.82f, updatedProduct.getFreightValue(), 0.01); // 验证运费是否更新
//         assertEquals("Tools", updatedProduct.getCategory()); // 验证类别是否更新
//         assertEquals(
//                 "New range of formal shirts are designed keeping you in mind. With fits and styling that will make you stand apart",
//                 updatedProduct.getDescription()); // 验证描述是否更新
//     }

//     // @Test
//     // void testUpdateProductPrice() throws Exception {
//     //     // 构造请求体的 JSON 字符串
//     //     String requestBody = "{"
//     //             + "\"sellerId\":51,"
//     //             + "\"productId\":2,"
//     //             + "\"price\":99.99,"
//     //             // + "\"version\":\"v2\","
//     //             + "\"instanceId\":\"test-instance-1\""
//     //             + "}";

//     //     // 模拟 HTTP PATCH 请求，更新产品价格
//     //     mockMvc.perform(patch("/product/")
//     //             .contentType(MediaType.APPLICATION_JSON)
//     //             .content(requestBody) // 使用手动构造的 JSON
//     //             .accept(MediaType.APPLICATION_JSON))
//     //             .andExpect(status().isAccepted()) // 期望状态码 202 Accepted
//     //             .andDo(print()); // 打印请求和响应内容

//     //     // 验证数据库中的产品价格是否更新
//     //     Product updatedProduct = productRepository.findById(new ProductId(51, 2)).orElse(null);
//     //     assertNotNull(updatedProduct, "Product should exist after price update");
//     //     assertEquals(99.99f, updatedProduct.getPrice(), "Product price should match the updated value");
//     //     assertEquals("0", updatedProduct.getVersion(), "Product version should be updated");
//     // }

// }
