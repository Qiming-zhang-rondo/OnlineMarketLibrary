package com.example.seller.service;

import com.example.seller.model.OrderSellerView;
import com.example.common.events.InvoiceIssued;
import com.example.common.events.ShipmentNotification;

public interface IMaterializedViewService{
    /**
     * 初始化物化视图（如系统启动后加载数据库数据到缓存）
     */
    void initializeMaterializedView();

    /**
     * 根据卖家ID获取统计视图数据
     */
    OrderSellerView getSellerView(int sellerId);

    /**
     * 处理发票下发事件，更新对应卖家统计数据
     */
    void processInvoiceIssued(InvoiceIssued invoiceIssued);

    /**
     * 处理发货通知事件，更新对应卖家统计数据
     */
    void processShipmentNotification(ShipmentNotification notification);
}
