package com.example.customer.service;

import com.example.common.events.DeliveryNotification;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.customer.model.Customer;
import com.example.customer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // deal with DeliveryNotification event
    public void processDeliveryNotification(DeliveryNotification deliveryNotification) {
        Customer customer = customerRepository.findById(deliveryNotification.getCustomerId());
        if (customer != null) {
            customer.setDeliveryCount(customer.getDeliveryCount() + 1);
            customerRepository.save(customer);
            logger.info("Processed delivery notification for customer: {}", deliveryNotification.getCustomerId());
        } else {
            logger.warn("Customer not found for delivery notification: {}", deliveryNotification.getCustomerId());
        }
    }

    // deal with PaymentConfirmed event
    public void processPaymentConfirmed(PaymentConfirmed paymentConfirmed) {
        logger.info("传递的ID是: {}",paymentConfirmed.getCustomer().getCustomerId());
        Customer customer = customerRepository.findById(paymentConfirmed.getCustomer().getCustomerId());
        if (customer != null) {
            customer.setSuccessPaymentCount(customer.getSuccessPaymentCount() + 1);
            customerRepository.save(customer);
            logger.info("Processed payment confirmation for customer: {}", paymentConfirmed.getCustomer().getCustomerId());
        } else {
            logger.warn("Customer not found for payment confirmation: {}", paymentConfirmed.getCustomer().getCustomerId());
        }
    }

    // deal with PaymentFailed event
    public void processPaymentFailed(PaymentFailed paymentFailed) {
        Customer customer = customerRepository.findById(paymentFailed.getCustomer().getCustomerId());
        if (customer != null) {
            customer.setFailedPaymentCount(customer.getFailedPaymentCount() + 1);
            customerRepository.save(customer);
            logger.info("Processed payment failure for customer: {}", paymentFailed.getCustomer().getCustomerId());
        } else {
            logger.warn("Customer not found for payment failure: {}", paymentFailed.getCustomer().getCustomerId());
        }
    }

    // clean
    public void cleanup() {
        logger.info("Performing cleanup operation");
        customerRepository.deleteAll();
    }

    // reset
    public void reset() {
        logger.info("Resetting customer data");
        customerRepository.reset();  
    }
}
