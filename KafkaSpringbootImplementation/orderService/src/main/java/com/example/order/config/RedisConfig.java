package com.example.order.config;

import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.model.OrderHistory;
import com.example.order.model.CustomerOrder;
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
        objectMapper.registerModule(new JavaTimeModule()); // 处理 Java 8 时间类型
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 日期序列化为字符串
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }

    private <T> RedisTemplate<String, T> createTemplate(RedisConnectionFactory factory, Class<T> clazz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(clazz);
        serializer.setObjectMapper(objectMapper());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public RedisTemplate<String, Order> orderRedisTemplate(RedisConnectionFactory factory) {
        return createTemplate(factory, Order.class);
    }

    @Bean
    public RedisTemplate<String, OrderItem> orderItemRedisTemplate(RedisConnectionFactory factory) {
        return createTemplate(factory, OrderItem.class);
    }

    @Bean
    public RedisTemplate<String, OrderHistory> orderHistoryRedisTemplate(RedisConnectionFactory factory) {
        return createTemplate(factory, OrderHistory.class);
    }

    @Bean
    public RedisTemplate<String, CustomerOrder> customerOrderRedisTemplate(RedisConnectionFactory factory) {
        return createTemplate(factory, CustomerOrder.class);
    }
}