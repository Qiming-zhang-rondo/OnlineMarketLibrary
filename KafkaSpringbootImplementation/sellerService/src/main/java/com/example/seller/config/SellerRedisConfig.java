package com.example.seller.config;

import com.example.seller.dto.SellerDashboard;
import com.example.seller.model.OrderEntry;
import com.example.seller.model.OrderSellerView;
import com.example.seller.model.Seller;
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
public class SellerRedisConfig {

    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册 JavaTimeModule 用于序列化 Java 8 时间类型
        objectMapper.registerModule(new JavaTimeModule());
        // 禁止将日期写成时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 设置字段可见性为ANY，方便序列化所有字段
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }

    /**
     * RedisTemplate 用于存储 Seller 对象，key 格式如 "seller:123"
     */
    @Bean
    public RedisTemplate<String, Seller> sellerRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Seller> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用指定的 ObjectMapper 与 Seller.class，直接调用构造函数
        Jackson2JsonRedisSerializer<Seller> serializer = new Jackson2JsonRedisSerializer<>(objectMapper(), Seller.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }

    /**
     * RedisTemplate 用于存储 OrderEntry 对象，
     * 例如 key 格式可以设计为 "orderEntry:customerId:orderId:sellerId:productId"
     */
    @Bean
    public RedisTemplate<String, OrderEntry> orderEntryRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, OrderEntry> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<OrderEntry> serializer = new Jackson2JsonRedisSerializer<>(objectMapper(), OrderEntry.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }

    /**
     * RedisTemplate 用于存储 OrderSellerView 对象，
     * 用于展示卖家汇总视图，key 格式如 "sellerDashboard:123"
     */
    @Bean
    public RedisTemplate<String, SellerDashboard> orderSellerViewRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, SellerDashboard> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<SellerDashboard> serializer = new Jackson2JsonRedisSerializer<>(objectMapper(), SellerDashboard.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }
}

