package com.example.stock.service;

import com.example.common.events.*;
import com.example.stock.model.StockItem;

public interface IStockService {

    void reserveStock(ReserveStock checkout);

    void confirmReservation(PaymentConfirmed payment);

    void cancelReservation(PaymentFailed paymentFailure);

    void processProductUpdate(ProductUpdated productUpdate);

    void createStockItem(StockItem stockItem);

    void increaseStock(IncreaseStock increaseStock);

    void cleanup();

    void reset();

    void processPoisonReserveStock(ReserveStock reserveStock);

    void processPoisonProductUpdate(ProductUpdated productUpdate);
}
