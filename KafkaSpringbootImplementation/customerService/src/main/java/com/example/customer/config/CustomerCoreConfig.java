package com.example.customer.config;

import com.example.customer.service.CustomerServiceCore;
import com.example.customer.repository.ICustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerCoreConfig {

    @Bean
    public CustomerServiceCore customerServiceCore(ICustomerRepository customerRepository) {
        return new CustomerServiceCore(customerRepository);
    }
}