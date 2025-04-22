package com.example.stock.config;

import com.example.stock.model.StockItem;
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
        // 禁用将日期写成时间戳
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 设置所有字段的可见性
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }

    /**
     * RedisTemplate for StockItem 对象
     * Key 为 String 类型，Value 为 StockItem 类型
     */
    @Bean
    public RedisTemplate<String, StockItem> stockRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, StockItem> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<StockItem> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper(), StockItem.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }
}
