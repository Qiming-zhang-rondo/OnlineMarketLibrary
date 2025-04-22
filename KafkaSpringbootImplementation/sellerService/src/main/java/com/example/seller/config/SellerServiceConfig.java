package com.example.seller.config;

import com.example.seller.repository.IOrderEntryRepository;
import com.example.seller.repository.RedisOrderEntryRepository;
import com.example.seller.repository.RedisOrderSellerViewRepository;
import com.example.seller.repository.RedisSellerRepository;
import com.example.seller.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SellerServiceConfig {

    @Bean
    public ISellerService sellerService(RedisSellerRepository sellerRepository,
                                        RedisOrderEntryRepository orderEntryRepository,
                                        RedisOrderSellerViewRepository orderSellerViewRepository,
                                        IMaterializedViewService materializedViewService
                                        ) {
        return new SellerServiceCore(sellerRepository, orderEntryRepository, orderSellerViewRepository, materializedViewService);
    }

    @Bean
    public IMaterializedViewService materializedViewService(IOrderEntryRepository orderEntryRepository,
                                                            IMaterializedViewServiceCache orderSellerViewCache) {
        return new MaterializedViewServiceCore(orderEntryRepository, orderSellerViewCache);
    }
}

