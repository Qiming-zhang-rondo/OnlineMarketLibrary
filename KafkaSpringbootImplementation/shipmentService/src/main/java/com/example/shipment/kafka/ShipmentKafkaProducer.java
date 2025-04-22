package com.example.shipment.kafka;

import com.example.common.driver.TransactionMark;
import com.example.common.events.DeliveryNotification;
import com.example.common.events.PaymentConfirmed;
import com.example.common.events.ShipmentNotification;
import com.example.common.messaging.IEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShipmentKafkaProducer implements IEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // 定义各个主题名称
    private static final String PAYMENT_CONFIRMED_TOPIC = "payment-confirmed-topic";
    private static final String DELIVERY_NOTIFICATION_TOPIC = "delivery-notification-topic";
    private static final String SHIPMENT_NOTIFICATION_TOPIC = "shipment-notification-topic";
    private static final String TRANSACTION_MARK_TOPIC = "TransactionMark_CUSTOMER_SESSION";

    @Override
    public void publishEvent(String topic, Object event) {
        sendAsJson(topic, event);
    }

    public void sendPaymentConfirmed(PaymentConfirmed paymentConfirmed) {
        sendAsJson(PAYMENT_CONFIRMED_TOPIC, paymentConfirmed);
        logger.info("Sent payment confirmed event to Kafka: {}", paymentConfirmed.getOrderId());
    }

    public void sendDeliveryNotification(DeliveryNotification deliveryNotification) {
        sendAsJson(DELIVERY_NOTIFICATION_TOPIC, deliveryNotification);
        logger.info("Sent delivery notification to Kafka for order ID: {}", deliveryNotification.getOrderId());
    }

    public void sendShipmentNotification(ShipmentNotification shipmentNotification) {
        sendAsJson(SHIPMENT_NOTIFICATION_TOPIC, shipmentNotification);
        logger.info("Sent shipment notification to Kafka, instanceId: {}", shipmentNotification.getInstanceId());
    }

    public void sendTransactionMark(TransactionMark transactionMark) {
        sendAsJson(TRANSACTION_MARK_TOPIC, transactionMark);
    }

    /**
     * 将消息对象序列化为 JSON 后发送到指定主题。
     */
    private void sendAsJson(String topic, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(new ProducerRecord<>(topic, json));
        } catch (Exception e) {
            logger.error("Failed to serialize and send message to topic {}: {}", topic, e.getMessage());
        }
    }
}

