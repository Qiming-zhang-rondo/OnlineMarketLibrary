package com.example.customer;

import com.example.customer.model.Customer;
import com.example.customer.repository.RedisCustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedisCustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        customerRepository.deleteAll(); // 清除 Redis 中旧数据
    }

    @Test
    public void testAddCustomer() throws Exception {
        Customer customer = new Customer();
        customer.setId(1); // Redis 无主键生成，需手动设置 ID
        customer.setFirstName("John");
        customer.setLastName("Doe");

        mockMvc.perform(post("/customer/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetCustomerById() throws Exception {
        Customer customer = new Customer();
        customer.setId(2);
        customer.setFirstName("Alice");
        customer.setLastName("Smith");
        customerRepository.save(customer);

        mockMvc.perform(get("/customer/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    public void testGetCustomerById_NotFound() throws Exception {
        mockMvc.perform(get("/customer/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCleanup() throws Exception {
        mockMvc.perform(patch("/customer/cleanup"))
                .andExpect(status().isAccepted());
    }

    @Test
    public void testReset() throws Exception {
        Customer customer = new Customer();
        customer.setId(3);
        customer.setFirstName("Bob");
        customer.setLastName("Brown");
        customer.setSuccessPaymentCount(5);
        customer.setFailedPaymentCount(3);
        customerRepository.save(customer);

        mockMvc.perform(patch("/customer/reset"))
                .andExpect(status().isAccepted());

        Customer updated = customerRepository.findById(3);
        assert updated.getSuccessPaymentCount() == 0;
        assert updated.getFailedPaymentCount() == 0;
    }
}