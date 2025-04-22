package com.example.seller.service;

import com.example.seller.model.OrderSellerView;

public interface IMaterializedViewServiceCache {
    /**
     * 获取卖家视图数据
     */
    OrderSellerView getSellerView(int sellerId);

    /**
     * 更新卖家视图数据
     */
    void updateSellerView(int sellerId, OrderSellerView view);

    /**
     * 清除所有相关的缓存数据
     */
    void clear();
}
