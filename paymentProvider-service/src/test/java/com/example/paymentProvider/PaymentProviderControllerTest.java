// package com.example.paymentProvider;

// import com.example.common.integration.Currency;
// import com.example.common.integration.PaymentIntentCreateOptions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;

// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest
// @AutoConfigureMockMvc
// class PaymentProviderControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @BeforeEach
//     void setUp() {
//         // 这里不需要使用 Mockito 初始化
//     }

//     @Test
//     void processPaymentIntent_ShouldReturnPaymentIntent() throws Exception {
//         // 创建请求参数
//         PaymentIntentCreateOptions options = new PaymentIntentCreateOptions();
//         options.setAmount(100.0f);
//         options.setCustomer("customer123");
//         options.setCurrency(Currency.USD);
//         options.setIdempotencyKey("uniqueKey123");

//         // 发送请求并验证响应
//         mockMvc.perform(post("/api/payment/esp")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(options)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").isNotEmpty()) // 验证生成的 PaymentIntent ID 存在
//                 .andExpect(jsonPath("$.amount").value(100.0))
//                 .andExpect(jsonPath("$.customer").value("customer123"))
//                 .andExpect(jsonPath("$.currency").value("USD"))
//                 .andExpect(jsonPath("$.status").value("succeeded")); // 假设返回状态是 succeeded
//     }
// }
