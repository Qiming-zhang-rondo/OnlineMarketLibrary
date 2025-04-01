package com.example.common.entities;

/**
 * It seems there is no explicit shipment status,
 * only package status. So we derived from dev olist 
 * (https://dev.olist.com/docs/fulfillment) a possible
 * set of statuses for shipment:
 */
public enum ShipmentStatus {
    // A shipment object is created as approved
    // originally approved only when packages are
    // created by sellers. But in the benchmark we
    // create them automatically as part of business logic
    APPROVED,

    // When at least one package is delivered
    DELIVERY_IN_PROGRESS,

    // When all packages are delivered
    CONCLUDED
}
