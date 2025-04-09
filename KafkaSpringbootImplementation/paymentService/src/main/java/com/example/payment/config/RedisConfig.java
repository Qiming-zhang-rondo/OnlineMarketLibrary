package com.example.payment.config;

import com.example.payment.model.OrderPayment;
import com.example.payment.model.OrderPaymentCard;
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
        objectMapper.registerModule(new JavaTimeModule()); // 支持 Java 8 日期时间
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 用字符串表示日期
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
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, OrderPayment> orderPaymentRedisTemplate(RedisConnectionFactory factory) {
        return createTemplate(factory, OrderPayment.class);
    }

    @Bean
    public RedisTemplate<String, OrderPaymentCard> orderPaymentCardRedisTemplate(RedisConnectionFactory factory) {
        return createTemplate(factory, OrderPaymentCard.class);
    }
}