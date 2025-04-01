// package com.example.customer;

// import com.example.customer.model.Customer;
// import com.example.customer.repository.CustomerRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// public class CustomerControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private CustomerRepository customerRepository;

//     @Autowired
//     private ObjectMapper objectMapper;

//     // @BeforeEach
//     // public void setup() {
//     //     // 清空数据库表
//     //     customerRepository.deleteAll();
//     // }

//     @Test
//     public void testAddCustomer() throws Exception {
//         Customer customer = new Customer();
//         customer.setFirstName("John");
//         customer.setLastName("Doe");

//         // 模拟发送 POST 请求，添加客户
//         mockMvc.perform(post("/customer/")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(customer)))
//                 .andExpect(status().isCreated());
//     }

//     @Test
//     public void testGetCustomerById() throws Exception {
//         Customer customer = new Customer();
//         customer.setFirstName("John");
//         customer.setLastName("Doe");
//         customer = customerRepository.save(customer);

//         // 模拟发送 GET 请求，获取客户详情
//         mockMvc.perform(get("/customer/" + customer.getId()))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").value(customer.getId()))
//                 .andExpect(jsonPath("$.firstName").value("John"))
//                 .andExpect(jsonPath("$.lastName").value("Doe"));
//     }

//     @Test
//     public void testGetCustomerById_NotFound() throws Exception {
//         // 模拟发送 GET 请求，验证客户不存在的情况
//         mockMvc.perform(get("/customer/999"))
//                 .andExpect(status().isNotFound());
//     }

//     // @Test
//     // public void testCleanup() throws Exception {
//     //     // 如果有需要实现的清理逻辑，可以在这里实现并测试
//     //     mockMvc.perform(patch("/customers/cleanup"))
//     //             .andExpect(status().isAccepted());
//     // }

//     // @Test
//     // public void testReset() throws Exception {
//     //     // 如果有需要实现的重置逻辑，可以在这里实现并测试
//     //     mockMvc.perform(patch("/customers/reset"))
//     //             .andExpect(status().isAccepted());
//     // }
// }
