package com.example.shipment.config;

import com.example.shipment.repository.IPackageRepository;
import com.example.shipment.repository.IShipmentRepository;
import com.example.shipment.kafka.ShipmentKafkaProducer;
import com.example.shipment.service.IShipmentService;
import com.example.shipment.service.ShipmentServiceCore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShipmentServiceConfig {

    @Bean
    public IShipmentService shipmentService(IShipmentRepository shipmentRepository,
                                           IPackageRepository packageRepository,
                                           ShipmentKafkaProducer shipmentKafkaProducer) {
        return new ShipmentServiceCore(shipmentRepository, packageRepository,shipmentKafkaProducer);
    }
}

