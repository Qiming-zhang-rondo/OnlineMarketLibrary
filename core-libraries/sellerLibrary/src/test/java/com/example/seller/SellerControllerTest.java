package com.example.seller;

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
public class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String sellerJson;

    @BeforeEach
    public void setup() {
        // 模拟请求 JSON 数据
        sellerJson = "{"
                + "\"id\":1,"
                + "\"name\":\"Test Seller\","
                + "\"companyName\":\"Test Company\","
                + "\"email\":\"seller@test.com\","
                + "\"phone\":\"1234567890\","
                + "\"mobilePhone\":\"0987654321\","
                + "\"cpf\":\"12345678901\","
                + "\"cnpj\":\"12345678901234\","
                + "\"address\":\"Test Address\","
                + "\"city\":\"Test City\","
                + "\"state\":\"Test State\","
                + "\"zipCode\":\"12345\""
                + "}";
    }

    @Test
    public void testAddSeller_Success() throws Exception {
        mockMvc.perform(post("/seller/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sellerJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetSeller_Success() throws Exception {
        mockMvc.perform(get("/seller/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Seller"));
    }

    @Test
    public void testGetSeller_NotFound() throws Exception {
        mockMvc.perform(get("/seller/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetDashboard_Success() throws Exception {
        mockMvc.perform(get("/seller/dashboard/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testReset_Success() throws Exception {
        mockMvc.perform(patch("/seller/reset"))
                .andExpect(status().isAccepted());
    }

    @Test
    public void testCleanup_Success() throws Exception {
        mockMvc.perform(patch("/seller/cleanup"))
                .andExpect(status().isAccepted());
    }
}
