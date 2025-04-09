package com.example.payment.service;

import com.example.common.driver.MarkStatus;
import com.example.common.driver.TransactionMark;
import com.example.common.driver.TransactionType;
import com.example.common.entities.OrderItem;
import com.example.common.entities.PaymentType;
import com.example.common.events.InvoiceIssued;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.PaymentFailed;
import com.example.common.integration.PaymentIntent;
import com.example.common.integration.PaymentIntentCreateOptions;
import com.example.common.integration.PaymentStatus;
import com.example.common.messaging.IEventPublisher;
import com.example.payment.config.IPaymentConfig;
import com.example.payment.model.OrderPayment;
import com.example.payment.model.OrderPaymentCard;
import com.example.payment.model.OrderPaymentCardId;
import com.example.payment.model.OrderPaymentId;
import com.example.payment.repository.IOrderPaymentCardRepository;
import com.example.payment.repository.IPaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PaymentServiceCore implements IPaymentService {

    private final IPaymentRepository paymentRepository;
    private final IOrderPaymentCardRepository orderPaymentCardRepository;
    private final IEventPublisher eventPublisher;
    private final IPaymentConfig config;
    private final IExternalProvider externalProvider;
    private final Logger logger = LoggerFactory.getLogger(PaymentServiceCore.class);

    public PaymentServiceCore(IPaymentRepository paymentRepository,
                               IOrderPaymentCardRepository orderPaymentCardRepository,
                               IEventPublisher eventPublisher,
                               IPaymentConfig config,
                               IExternalProvider externalProvider) {
        this.paymentRepository = paymentRepository;
        this.orderPaymentCardRepository = orderPaymentCardRepository;
        this.eventPublisher = eventPublisher;
        this.config = config;
        this.externalProvider = externalProvider;
    }

    @Override
    public void processPayment(InvoiceIssued invoiceIssued) {
        try {
            logger.info("Start processing payment for order ID: {}, customer ID: {}, localDateTime: {}",
                    invoiceIssued.getOrderId(), invoiceIssued.getCustomer().getCustomerId(),
                    invoiceIssued.getIssueDate());

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
                        cardExpParsed.getYear()
                );

                PaymentIntent intent = externalProvider.create(options);
                if (intent == null) {
                    logger.error("Failed to obtain payment intent from external provider.");
                    throw new RuntimeException("Cannot get payment intent from external provider");
                }
                status = "succeeded".equals(intent.getStatus()) ? PaymentStatus.SUCCEEDED : PaymentStatus.REQUIRES_PAYMENT_METHOD;
                logger.info("External payment provider returned status: {}", intent.getStatus());
            } else {
                status = PaymentStatus.SUCCEEDED;
                logger.info("Payment provider disabled; assuming payment succeeded.");
            }

            LocalDateTime now = LocalDateTime.now();
            int seq = 1;
            boolean isCreditCard = PaymentType.CREDIT_CARD.name().equals(invoiceIssued.getCustomer().getPaymentType());
            logger.info("Payment type: {}", invoiceIssued.getCustomer().getPaymentType());

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
                paymentRepository.save(cardPaymentLine);
                OrderPayment entity = cardPaymentLine; 

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
                        seq++;
                    }
                }
            }

            if (!paymentLines.isEmpty()) {
                paymentRepository.saveAll(paymentLines);
            }
    

            if (config.isStreaming()) {
                if (status == PaymentStatus.SUCCEEDED) {
                    PaymentConfirmed paymentConfirmed = new PaymentConfirmed(
                            invoiceIssued.getCustomer(),
                            invoiceIssued.getOrderId(),
                            invoiceIssued.getTotalInvoice(),
                            invoiceIssued.getItems(),
                            now,
                            invoiceIssued.getInstanceId());
                    eventPublisher.publishEvent("payment-confirmed-topic", paymentConfirmed);
                } else {
                    PaymentFailed paymentFailed = new PaymentFailed(
                            status.name(),
                            invoiceIssued.getCustomer(),
                            invoiceIssued.getOrderId(),
                            invoiceIssued.getItems(),
                            invoiceIssued.getTotalInvoice(),
                            invoiceIssued.getInstanceId());
                    eventPublisher.publishEvent("payment-failed-topic", paymentFailed);
                    TransactionMark transactionMark = new TransactionMark(
                            invoiceIssued.getInstanceId(),
                            TransactionType.CUSTOMER_SESSION,
                            invoiceIssued.getCustomer().getCustomerId(),
                            MarkStatus.NOT_ACCEPTED,
                            "payment");
                    eventPublisher.publishEvent("TransactionMark_CUSTOMER_SESSION", transactionMark);
                }
            }

        } catch (Exception e) {
            logger.error("Error processing payment for order ID: {}, customer ID: {}",
                    invoiceIssued.getOrderId(), invoiceIssued.getCustomer().getCustomerId(), e);
            throw e;
        }
    }

    @Override
    public void processPoisonPayment(InvoiceIssued invoiceIssued) {
        TransactionMark transactionMark = new TransactionMark(
                invoiceIssued.getInstanceId(),
                TransactionType.CUSTOMER_SESSION,
                invoiceIssued.getCustomer().getCustomerId(),
                MarkStatus.ABORT,
                "payment");
        eventPublisher.publishEvent("TransactionMark_CUSTOMER_SESSION", transactionMark);
    }

    @Override
    public void cleanup() {
        orderPaymentCardRepository.deleteAll();
        paymentRepository.deleteAll();
    }
}
