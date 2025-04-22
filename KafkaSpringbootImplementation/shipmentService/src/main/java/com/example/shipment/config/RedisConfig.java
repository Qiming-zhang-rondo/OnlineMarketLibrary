package com.example.shipment.config;

import com.example.shipment.model.Shipment;
import com.example.shipment.model.Package;
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
        ObjectMapper mapper = new ObjectMapper();
        // 注册 JavaTimeModule 支持 Java 8 日期时间类型
        mapper.registerModule(new JavaTimeModule());
        // 禁用将日期写成时间戳的功能
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 设置所有字段的可见性
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }

    /**
     * RedisTemplate for Shipment 对象
     * Key 为 String 类型，Value 为 Shipment 类型
     */
    @Bean
    public RedisTemplate<String, Shipment> shipmentRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Shipment> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<Shipment> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper(), Shipment.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }

    @Bean
    public RedisTemplate<String, Package> packageRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Package> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<Package> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper(), Package.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
