package com.example.customer;

import java.time.LocalDateTime;

import com.example.customer.model.Customer;
import com.example.customer.repository.RedisCustomerRepository;
import com.example.common.events.DeliveryNotification;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.common.requests.CustomerCheckout;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext
public class CustomerServiceTest {

    @Autowired
    private RedisCustomerRepository customerRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        customerRepository.deleteAll();
    }

    @Test
    public void testProcessPaymentConfirmed() throws Exception {
        Customer customer = createCustomer(1);
        customerRepository.save(customer);

        CustomerCheckout checkout = new CustomerCheckout();
        checkout.setCustomerId(customer.getId());

        PaymentConfirmed event = new PaymentConfirmed();
        event.setCustomer(checkout);

        kafkaTemplate.send("payment-confirmed-topic", objectMapper.writeValueAsString(event));
        Thread.sleep(300);

        Customer updated = customerRepository.findById(customer.getId());
        assertEquals(1, updated.getSuccessPaymentCount());
    }

    @Test
    public void testProcessPaymentFailed() throws Exception {
        Customer customer = createCustomer(2);
        customerRepository.save(customer);

        CustomerCheckout checkout = new CustomerCheckout();
        checkout.setCustomerId(customer.getId());

        PaymentFailed event = new PaymentFailed();
        event.setCustomer(checkout);

        kafkaTemplate.send("payment-failed-topic", objectMapper.writeValueAsString(event));
        Thread.sleep(300);

        Customer updated = customerRepository.findById(customer.getId());
        assertEquals(1, updated.getFailedPaymentCount());
    }

    @Test
    public void testProcessDeliveryNotification() throws Exception {
        Customer customer = createCustomer(3);
        customerRepository.save(customer);

        DeliveryNotification event = new DeliveryNotification();
        event.setCustomerId(customer.getId());
        event.setOrderId(1001);
        event.setProductId(2002);

        kafkaTemplate.send("delivery-notification-topic", objectMapper.writeValueAsString(event));
        Thread.sleep(300);

        Customer updated = customerRepository.findById(customer.getId());
        assertEquals(1, updated.getDeliveryCount());
    }

    private Customer createCustomer(int id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName("User" + id);
        customer.setLastName("Test");
        customer.setCardHolderName("Holder " + id);
        customer.setCardNumber("123456789" + id);
        customer.setCardExpiration("12/25");
        customer.setCardSecurityNumber("123");
        customer.setCardType("VISA");
        customer.setSuccessPaymentCount(0);
        customer.setFailedPaymentCount(0);
        customer.setDeliveryCount(0);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        return customer;
    }
}