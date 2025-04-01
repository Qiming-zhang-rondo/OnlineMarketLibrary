package com.example.shipment.controller;

import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentId;
import com.example.shipment.service.ShipmentService;
import com.example.shipment.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/shipment")
public class ShipmentController {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentController.class);

    private final ShipmentService shipmentService;
    private final ShipmentRepository shipmentRepository;

    public ShipmentController(ShipmentService shipmentService, ShipmentRepository shipmentRepository) {
        this.shipmentService = shipmentService;
        this.shipmentRepository = shipmentRepository;
    }

    @PatchMapping("/{instanceId}")
    public ResponseEntity<Void> updateShipment(@PathVariable String instanceId) throws Exception {
        shipmentService.updateShipment(instanceId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{customerId}/{orderId}")
    public ResponseEntity<Shipment> getShipment(@PathVariable int customerId, @PathVariable int orderId) {
        Optional<Shipment> shipmentOpt = shipmentRepository.findById(new ShipmentId(customerId, orderId));
        return shipmentOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/cleanup")
    public ResponseEntity<Void> cleanup() {
        logger.warn("Cleanup requested at {}", System.currentTimeMillis());
        shipmentService.Cleanup();
        return ResponseEntity.ok().build();
    }

}
