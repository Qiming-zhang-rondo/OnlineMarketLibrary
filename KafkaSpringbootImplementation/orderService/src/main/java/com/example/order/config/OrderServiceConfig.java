package com.example.order.config;

import com.example.common.messaging.IEventPublisher;
import com.example.order.service.OrderServiceCore;
import com.example.order.repository.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderServiceConfig {

    @Bean
    public OrderServiceCore orderServiceCore(
            RedisOrderRepository orderRepository,
            RedisOrderItemRepository orderItemRepository,
            RedisOrderHistoryRepository orderHistoryRepository,
            RedisCustomerOrderRepository customerOrderRepository,
            IEventPublisher eventPublisher) {
        return new OrderServiceCore(
                orderRepository,
                orderItemRepository,
                orderHistoryRepository,
                customerOrderRepository,
                eventPublisher
        );
    }
}