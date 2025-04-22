package com.example.stock.config;

import com.example.stock.repository.IStockRepository;
import com.example.stock.kafka.StockKafkaProducer;
import com.example.stock.service.IStockService;
import com.example.stock.service.StockServiceCore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockServiceConfig {

    @Bean
    public IStockService stockService(IStockRepository stockRepository,
                                      IStockConfig stockConfig,
                                      StockKafkaProducer stockKafkaProducer) {
        return new StockServiceCore(stockRepository, stockConfig, stockKafkaProducer);
    }
}

