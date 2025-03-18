package com.example.order.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.common.driver.TransactionMark;
import com.example.common.events.InvoiceIssued;

@Service
public class OrderKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;  

    private static final String INVOICE_ISSUED_TOPIC = "invoice-issued-topic";
    private static final String TRANSACTION_TOPIC = "TransactionMark_CUSTOMER_SESSION";  


    /**
     * send InvoiceIssued to Kafka
     */
    public void sendInvoiceIssued(InvoiceIssued invoiceIssued) {
        logger.info("Sending InvoiceIssued event for orderId = {}", invoiceIssued.getOrderId());
        kafkaTemplate.send(INVOICE_ISSUED_TOPIC, invoiceIssued);  
    }

    public void sendTransactionMark(TransactionMark transactionMark) {
        logger.info("发送交易标记: {}", transactionMark);
        kafkaTemplate.send(TRANSACTION_TOPIC, transactionMark);  
    }
}
