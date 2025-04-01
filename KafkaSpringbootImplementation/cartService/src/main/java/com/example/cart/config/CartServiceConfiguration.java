package com.example.cart.config;

import com.example.cart.repository.RedisCartItemRepository;
import com.example.cart.repository.RedisCartRepository;
import com.example.cart.repository.RedisProductReplicaRepository;
import com.example.cart.kafka.CartKafkaProducer;
import com.example.cart.service.CartServiceCore;
import com.example.cart.service.ICartService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CartServiceConfiguration {

    @Bean
    public ICartService cartService(RedisCartRepository cartRepository,
                                    RedisCartItemRepository cartItemRepository,
                                    RedisProductReplicaRepository productReplicaRepository,
                                    CartKafkaProducer cartKafkaProducer) {
        return new CartServiceCore(cartRepository, cartItemRepository, productReplicaRepository, cartKafkaProducer);
    }
}