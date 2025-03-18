package com.example.payment.controller;

import com.example.common.events.InvoiceIssued;
import com.example.payment.model.OrderPayment;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

   
    @PostMapping("/processPayment")
    public ResponseEntity<Void> processPaymentManually(@RequestBody InvoiceIssued invoiceIssued) {
        try {
            
            paymentService.processPayment(invoiceIssued);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while processing payment: ", e);
            paymentService.processPoisonPayment(invoiceIssued);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   
    @GetMapping("/{customerId}/{orderId}")
    public ResponseEntity<List<OrderPayment>> getPaymentByOrderId(@PathVariable int customerId, @PathVariable int orderId) {
        List<OrderPayment> payments = paymentRepository.findAllByCustomerIdAndOrderId(customerId, orderId);
        return payments != null && !payments.isEmpty()
                ? new ResponseEntity<>(payments, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    
    @PatchMapping("/cleanup")
    public ResponseEntity<Void> cleanup() {
        logger.warn("Cleanup requested at {}", System.currentTimeMillis());
        paymentService.cleanup();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
