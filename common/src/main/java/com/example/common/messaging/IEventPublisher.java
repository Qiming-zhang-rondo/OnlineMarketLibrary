package com.example.common.messaging;

/**
 * Unified event publishing interface, all middleware (Kafka, Orleans) should implement this interface
 */
public interface IEventPublisher {
    void publishEvent(String topic, Object event);
}