package com.example.seller.service;

import com.example.common.entities.OrderStatus;
import com.example.common.events.InvoiceIssued;
import com.example.common.events.ShipmentNotification;
import com.example.seller.model.OrderSellerView;
import com.example.seller.repository.IOrderEntryRepository;
import com.example.seller.repository.IOrderSellerViewRepository;

import java.util.Arrays;
import java.util.List;

/**
 * 基于数据库实现的物化视图核心逻辑。
 *
 * 此实现使用抽象的 IOrderEntryRepository 来查询订单统计，
 * 并通过 IOrderSellerViewRepository 对物化视图数据进行清空和重新填充。
 *
 * 注意：本实现不直接查询物化视图数据（IOrderSellerViewRepository 接口中没有提供查询方法），
 * 所以在 getSellerView() 中采用调用 IOrderEntryRepository.findAllSellerAggregates() 来聚合数据。
 */
public class MaterializedViewServiceCacheCore implements IMaterializedViewService {

    private final IOrderEntryRepository orderEntryRepository;
    private final IOrderSellerViewRepository orderSellerViewRepository;

    public MaterializedViewServiceCacheCore(IOrderEntryRepository orderEntryRepository,
                                            IOrderSellerViewRepository orderSellerViewRepository) {
        this.orderEntryRepository = orderEntryRepository;
        this.orderSellerViewRepository = orderSellerViewRepository;
    }

    /**
     * 初始化物化视图：
     * 先清空已有视图数据，然后调用 populateMaterializedView() 将最新的聚合数据填充到物化视图中。
     */
    @Override
    public void initializeMaterializedView() {
        orderSellerViewRepository.clearMaterializedView();
        orderSellerViewRepository.populateMaterializedView();
    }

    /**
     * 获取指定卖家的仪表盘数据。
     *
     * 本实现调用 orderEntryRepository.findAllSellerAggregates，
     * 然后根据 sellerId 过滤对应的聚合数据并构造 OrderSellerView 对象。
     *
     * 如果找不到数据，返回 null。
     */
    @Override
    public OrderSellerView getSellerView(int sellerId) {
        // 定义需要统计的订单状态（可根据业务需要调整）
        List<OrderStatus> statuses = Arrays.asList(
                OrderStatus.INVOICED,
                OrderStatus.PAYMENT_PROCESSED,
                OrderStatus.READY_FOR_SHIPMENT,
                OrderStatus.IN_TRANSIT);

        List<Object[]> aggregates = orderEntryRepository.findAllSellerAggregates(statuses);
        // 每条数据格式假设为：
        // [sellerId (Integer), countOrders (Long), countItems (Long), totalAmount (Double),
        //  totalFreight (Double), totalInvoice (Double)]
        for (Object[] row : aggregates) {
            Integer id = (Integer) row[0];
            if (id != null && id == sellerId) {
                OrderSellerView view = new OrderSellerView();
                view.setSellerId(sellerId);
                view.setCountOrders(((Long) row[1]).intValue());
                view.setCountItems(((Long) row[2]).intValue());
                view.setTotalAmount(((Double) row[3]).floatValue());
                view.setTotalFreight(((Double) row[4]).floatValue());
                view.setTotalInvoice(((Double) row[5]).floatValue());
                return view;
            }
        }
        return null;
    }

    /**
     * 处理 InvoiceIssued 事件，更新物化视图。
     *
     * 此处采用简单处理：直接调用 IOrderSellerViewRepository.populateMaterializedView() 以重新填充数据。
     * 当然，根据业务要求可以做局部更新。
     */
    @Override
    public void processInvoiceIssued(InvoiceIssued invoiceIssued) {
        // 如果事件中的订单明细为 null，则打印错误日志并返回
        if (invoiceIssued.getItems() == null) {
            // 记录错误信息（实际代码可使用日志记录器）
            System.err.println("InvoiceIssued items are null. Event: " + invoiceIssued);
            return;
        }
        // 这里可以针对受影响的卖家进行局部更新，
        // 但为了简化逻辑，我们重新填充整个物化视图
        orderSellerViewRepository.populateMaterializedView();
    }

    /**
     * 处理 ShipmentNotification 事件，更新物化视图。
     *
     * 同样地，采用重新填充整个视图的方式更新数据。
     */
    @Override
    public void processShipmentNotification(ShipmentNotification notification) {
        orderSellerViewRepository.populateMaterializedView();
    }
}
