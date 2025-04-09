package com.example.payment;

import com.example.common.entities.OrderItem;
import com.example.common.events.InvoiceIssued;
import com.example.common.requests.CustomerCheckout;
import com.example.payment.model.OrderPayment;
import com.example.payment.repository.IPaymentRepository;
import com.example.payment.service.IPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentServiceTest {

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private IPaymentRepository paymentRepository;

    private InvoiceIssued invoiceIssued;

    @BeforeEach
    public void setup() {
        paymentRepository.deleteAll();

        CustomerCheckout customer = new CustomerCheckout();
        customer.setCustomerId(12345);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setStreet("123 Main St");
        customer.setCity("New York");
        customer.setState("NY");
        customer.setZipCode("10001");
        customer.setCardNumber("4111111111111111");
        customer.setCardHolderName("John Doe");
        customer.setCardExpiration("1230");
        customer.setCardSecurityNumber("123");
        customer.setCardBrand("VISA");
        customer.setPaymentType("CREDIT_CARD");
        customer.setInstallments(1);
        customer.setInstanceId("instance-001");

        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(101);
        item.setProductName("Example Product");
        item.setQuantity(2);
        item.setUnitPrice(50.25f);
        item.setFreightValue(10.25f);
        items.add(item);

        invoiceIssued = new InvoiceIssued(customer, 123, "INV-001",
                LocalDateTime.now(), 150.75f, items, "instance-001");
    }

    @Test
    public void testProcessPayment() {
        paymentService.processPayment(invoiceIssued);

        List<OrderPayment> payments = paymentRepository.findAllByCustomerIdAndOrderId(
                invoiceIssued.getCustomer().getCustomerId(),
                invoiceIssued.getOrderId());

        assertFalse(payments.isEmpty());
        assertEquals(1, payments.size());
        assertEquals(invoiceIssued.getTotalInvoice(), payments.get(0).getValue());
        OrderPayment payment = payments.get(0);
        assertNotNull(payment.getCreatedAt());
        assertTrue(payment.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    public void testCleanup() {
        paymentService.processPayment(invoiceIssued);
        paymentService.cleanup();

        List<OrderPayment> payments = paymentRepository.findAllByCustomerIdAndOrderId(
                invoiceIssued.getCustomer().getCustomerId(),
                invoiceIssued.getOrderId());
        assertTrue(payments.isEmpty());
    }
}
