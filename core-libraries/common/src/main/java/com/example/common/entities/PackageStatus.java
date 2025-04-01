package com.example.common.entities;

/**
 * Package status list:
 * Fulfillment is a complex (and unfit) process to embrace in a benchmark.
 * In this sense, we only use two of the statuses. Our adaptations are as follows:
 * Whenever packages are created, they are in shipped status.
 * Later on, the delivery transaction updates (some of) them to delivered.
 */
public enum PackageStatus {
    CREATED,
    READY_TO_SHIP,
    CANCELED,
    SHIPPED,
    LOST,
    STOLEN,
    SEIZED_FOR_INSPECTION,
    RETURNING_TO_SENDER,
    RETURNED_TO_SENDER,
    AWAITING_PICKUP_BY_RECEIVER,
    DELIVERED
}
