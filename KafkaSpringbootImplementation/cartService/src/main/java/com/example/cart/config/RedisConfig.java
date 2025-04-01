package com.example.cart.config;

import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import com.example.cart.model.ProductReplica;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); 
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }

    // RedisTemplate for Cart
    @Bean
    public RedisTemplate<String, Cart> cartRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Cart> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<Cart> serializer = new Jackson2JsonRedisSerializer<>(Cart.class);
        serializer.setObjectMapper(objectMapper());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }

    // RedisTemplate for CartItem
    @Bean
    public RedisTemplate<String, CartItem> cartItemRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, CartItem> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<CartItem> serializer = new Jackson2JsonRedisSerializer<>(CartItem.class);
        serializer.setObjectMapper(objectMapper());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }

    // RedisTemplate for product replica
    @Bean
    public RedisTemplate<String, ProductReplica> productReplicaRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ProductReplica> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<ProductReplica> serializer = new Jackson2JsonRedisSerializer<>(
                ProductReplica.class);
        serializer.setObjectMapper(objectMapper());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }

}