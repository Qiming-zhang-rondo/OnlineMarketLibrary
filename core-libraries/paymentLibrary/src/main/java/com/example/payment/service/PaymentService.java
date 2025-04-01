package com.example.payment.service;

import com.example.common.driver.MarkStatus;
import com.example.common.driver.TransactionMark;
import com.example.common.driver.TransactionType;
import com.example.common.entities.OrderItem;
import com.example.common.entities.PaymentType;
import com.example.common.events.InvoiceIssued;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.payment.infra.PaymentConfig;
import com.example.payment.kafka.PaymentKafkaProducer;
import com.example.payment.model.OrderPayment;
import com.example.payment.model.OrderPaymentCard;
import com.example.payment.model.OrderPaymentCardId;
import com.example.payment.model.OrderPaymentId;
import com.example.common.integration.*;
import com.example.payment.repository.OrderPaymentCardRepository;
import com.example.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderPaymentCardRepository orderPaymentCardRepository;
    private final PaymentKafkaProducer kafkaProducer; // Kafka Producer
    private final PaymentConfig config;
    private final ExternalProviderProxy externalProvider;
    private final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, OrderPaymentCardRepository orderPaymentCardRepository,
            PaymentKafkaProducer kafkaProducer, PaymentConfig config, ExternalProviderProxy externalProvider) {
        this.paymentRepository = paymentRepository;
        this.kafkaProducer = kafkaProducer;
        this.orderPaymentCardRepository = orderPaymentCardRepository;
        this.config = config;
        this.externalProvider = externalProvider;
    }

    @Transactional
    @Override
    public void processPayment(InvoiceIssued invoiceIssued) {
        try {
            logger.info("Start processing payment for order ID: {}, customer ID: {}, localDateTime: {}",
                    invoiceIssued.getOrderId(), invoiceIssued.getCustomer().getCustomerId(),
                    invoiceIssued.getIssueDate());

            // payment method 
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyy");
            YearMonth yearMonth = YearMonth.parse(invoiceIssued.getCustomer().getCardExpiration(), formatter);
            LocalDateTime cardExpParsed = yearMonth.atDay(1).atStartOfDay();
            logger.info("Parsed card expiration date: {}", cardExpParsed);

            PaymentStatus status;
            if (config.isPaymentProvider()) {
                logger.info("Using external payment provider for processing.");
                PaymentIntentCreateOptions options = new PaymentIntentCreateOptions(
                        invoiceIssued.getTotalInvoice(),
                        String.valueOf(invoiceIssued.getCustomer().getCustomerId()),
                        invoiceIssued.getInvoiceNumber(),
                        invoiceIssued.getCustomer().getCardNumber(),
                        invoiceIssued.getCustomer().getCardSecurityNumber(),
                        cardExpParsed.getMonthValue(),
                        cardExpParsed.getYear());

                PaymentIntent intent = externalProvider.create(options);
                if (intent == null) {
                    logger.error("Failed to obtain payment intent from external provider.");
                    throw new RuntimeException("Cannot get payment intent from external provider");
                }
                status = "succeeded".equals(intent.getStatus()) ? PaymentStatus.SUCCEEDED
                        : PaymentStatus.REQUIRES_PAYMENT_METHOD;
                logger.info("External payment provider returned status: {}", intent.getStatus());
            } else {
                status = PaymentStatus.SUCCEEDED;
                logger.info("Payment provider disabled; assuming payment succeeded.");
            }

            LocalDateTime now = LocalDateTime.now();
            int seq = 1;
            boolean isCreditCard = PaymentType.CREDIT_CARD.name().equals(invoiceIssued.getCustomer().getPaymentType());
            logger.info("Payment type: {}", invoiceIssued.getCustomer().getPaymentType());

            // 创建支付记录
            if (isCreditCard || PaymentType.DEBIT_CARD.name().equals(invoiceIssued.getCustomer().getPaymentType())) {
                OrderPaymentId orderPaymentId = new OrderPaymentId(
                        invoiceIssued.getCustomer().getCustomerId(),
                        invoiceIssued.getOrderId(),
                        seq);

                OrderPayment cardPaymentLine = new OrderPayment(
                        orderPaymentId,
                        invoiceIssued.getCustomer().getInstallments(),
                        invoiceIssued.getTotalInvoice(),
                        isCreditCard ? PaymentType.CREDIT_CARD : PaymentType.DEBIT_CARD,
                        status,
                        now);
                OrderPayment entity = paymentRepository.save(cardPaymentLine);
                logger.info("Saved payment record for order ID: {} with payment type: {}",
                        invoiceIssued.getOrderId(), cardPaymentLine.getType());

                
                OrderPaymentCardId orderPaymentCardId = new OrderPaymentCardId(
                        invoiceIssued.getCustomer().getCustomerId(),
                        invoiceIssued.getOrderId(),
                        seq);

                OrderPaymentCard card = new OrderPaymentCard();
                card.setId(orderPaymentCardId);
                card.setCardNumber(invoiceIssued.getCustomer().getCardNumber());
                card.setCardHolderName(invoiceIssued.getCustomer().getCardHolderName());
                card.setCardExpiration(cardExpParsed);
                card.setCardBrand(invoiceIssued.getCustomer().getCardBrand());
                card.setOrderPayment(entity);
                orderPaymentCardRepository.save(card);
                logger.info("Saved credit card details for customer ID: {}",
                        invoiceIssued.getCustomer().getCustomerId());
                seq++;
            }

            List<OrderPayment> paymentLines = new ArrayList<>();
            if (PaymentType.BOLETO.name().equals(invoiceIssued.getCustomer().getPaymentType())) {
                OrderPaymentId orderPaymentId = new OrderPaymentId(
                        invoiceIssued.getCustomer().getCustomerId(),
                        invoiceIssued.getOrderId(),
                        seq);
                paymentLines.add(new OrderPayment(
                        orderPaymentId,
                        1,
                        invoiceIssued.getTotalInvoice(),
                        PaymentType.BOLETO,
                        status,
                        now));
                logger.info("Added BOLETO payment line for order ID: {}", invoiceIssued.getOrderId());
                seq++;
            }

            
            if (status == PaymentStatus.SUCCEEDED) {
                for (OrderItem item : invoiceIssued.getItems()) {
                    if (item.getTotalIncentive() > 0) {
                        OrderPaymentId orderPaymentId = new OrderPaymentId(
                                invoiceIssued.getCustomer().getCustomerId(),
                                invoiceIssued.getOrderId(),
                                seq);

                        OrderPayment voucherPayment = new OrderPayment(
                                orderPaymentId,
                                1,
                                item.getTotalIncentive(),
                                PaymentType.VOUCHER,
                                status,
                                now);
                        paymentLines.add(voucherPayment);
                        logger.info("Added voucher payment for item: {}, amount: {}",
                                item.getProductId(), item.getTotalIncentive());
                        seq++;
                    }
                }
            }

            if (!paymentLines.isEmpty()) {
                paymentRepository.saveAll(paymentLines);
                logger.info("Saved all payment lines for order ID: {}", invoiceIssued.getOrderId());
            }
            paymentRepository.flush();

            // publish Kafka event
            if (config.isStreaming()) {
                if (status == PaymentStatus.SUCCEEDED) {
                    PaymentConfirmed paymentConfirmed = new PaymentConfirmed(
                            invoiceIssued.getCustomer(),
                            invoiceIssued.getOrderId(),
                            invoiceIssued.getTotalInvoice(),
                            invoiceIssued.getItems(),
                            now,
                            invoiceIssued.getInstanceId());
                    kafkaProducer.sendPaymentConfirmedEvent(paymentConfirmed);
                    logger.info("Sent PaymentConfirmed event for order ID: {}", invoiceIssued.getOrderId());
                } else {
                    PaymentFailed paymentFailed = new PaymentFailed(
                            status.name(),
                            invoiceIssued.getCustomer(),
                            invoiceIssued.getOrderId(),
                            invoiceIssued.getItems(),
                            invoiceIssued.getTotalInvoice(),
                            invoiceIssued.getInstanceId());
                    kafkaProducer.sendPaymentFailedEvent(paymentFailed);
                    TransactionMark transactionMark = new TransactionMark(
                            invoiceIssued.getInstanceId(),
                            TransactionType.CUSTOMER_SESSION,
                            invoiceIssued.getCustomer().getCustomerId(),
                            MarkStatus.NOT_ACCEPTED,
                            "payment");
                    kafkaProducer.sendPoisonPaymentEvent(transactionMark);
                    logger.info("Sent PaymentFailed and PoisonPayment events for order ID: {}",
                            invoiceIssued.getOrderId());
                }
            }
        } catch (Exception e) {
            logger.error("Error processing payment for order ID: {}, customer ID: {}",
                    invoiceIssued.getOrderId(), invoiceIssued.getCustomer().getCustomerId(), e);
            throw e;
        }
    }

    public void processPoisonPayment(InvoiceIssued invoiceIssued) {
        // Publish TransactionMark for poison payment
        TransactionMark transactionMark = new TransactionMark(
                invoiceIssued.getInstanceId(),
                TransactionType.CUSTOMER_SESSION,
                invoiceIssued.getCustomer().getCustomerId(),
                MarkStatus.ABORT,
                "payment");
        kafkaProducer.sendPoisonPaymentEvent(transactionMark);
    }

    @Override
    public void cleanup() {
        orderPaymentCardRepository.deleteAll();
        paymentRepository.deleteAll();
    }
}
