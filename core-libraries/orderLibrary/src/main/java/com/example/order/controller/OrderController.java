package com.example.order.controller;

import com.example.order.model.Order;
import com.example.order.service.IOrderService;
import com.example.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final IOrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(IOrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Order>> getByCustomerId(@PathVariable int customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/cleanup")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Void> cleanup() {
        logger.warn("Cleanup requested at {}", System.currentTimeMillis());
        orderService.cleanup();
        return ResponseEntity.accepted().build();
    }
}
