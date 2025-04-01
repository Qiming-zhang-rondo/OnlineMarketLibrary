package com.example.customer.controller;

import com.example.customer.model.Customer;
import com.example.customer.repository.CustomerRepository;
import com.example.customer.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    public CustomerController(CustomerService customerService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addCustomer(@RequestBody Customer customer) {
        logger.info("[AddCustomer] received for id {}", customer.getId());
        customerRepository.save(customer);
        logger.info("[AddCustomer] completed for id {}", customer.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable int customerId) {
        logger.info("[GetCustomerById] received for customer ID {}", customerId);
        Optional<Customer> customer = Optional.ofNullable(customerRepository.findById(customerId));
        
        if (customer.isPresent()) {
            logger.info("[GetCustomerById] completed for customer ID {}", customerId);
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
            logger.warn("[GetCustomerById] customer not found for ID {}", customerId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/cleanup")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Void> cleanup() {
        logger.warn("Cleanup requested at {}", System.currentTimeMillis());
        customerService.cleanup();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("/reset")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Void> reset() {
        logger.warn("Reset requested at {}", System.currentTimeMillis());
        customerService.reset();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
