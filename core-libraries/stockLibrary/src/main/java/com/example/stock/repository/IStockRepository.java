package com.example.stock.repository;

import com.example.stock.model.StockItem;
import com.example.stock.model.StockItemId;
import java.util.List;
import java.util.Optional;

public interface IStockRepository {

    /**
     * 获取指定 sellerId 与 productId 的库存项，
     * 使用悲观锁确保数据一致性。
     */
    StockItem findForUpdate(int sellerId, int productId);

    /**
     * 根据多个 StockItemId 批量查询库存项。
     */
    List<StockItem> findItemsByIds(List<StockItemId> ids);

    /**
     * 根据 sellerId 和 productId 查询库存项。
     */
    Optional<StockItem> findById(StockItemId stockItemId);

    Optional<StockItem> findById(int sellerId, int productId);

    /**
     * 根据 sellerId 查询所有库存项。
     */
    List<StockItem> findBySellerId(int sellerId);

    /**
     * 重置库存，将所有库存项设置为活跃状态，
     * 将版本置为 '0'，保留数量归零，并将可用数量更新为指定值。
     */
    void reset(int qty);

    void save(StockItem stockItem);

    void saveAll(List<StockItem> stockItemsReserved);

    void flush();


    void deleteAll();

}

