package com.example.seller.repository;

import com.example.seller.dto.SellerDashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisOrderSellerViewRepository implements IOrderSellerViewRepository {

    //修改OrderSellerView 实现Serializable 接口

    private static final String DASHBOARD_KEY = "sellerDashboard:";

    @Autowired
    private RedisTemplate<String, SellerDashboard> redisTemplate;

    public void saveDashboard(SellerDashboard dashboard) {
        String key = DASHBOARD_KEY + dashboard.getSellerView().getSellerId();
        redisTemplate.opsForValue().set(key, dashboard, 1, TimeUnit.HOURS);
    }

    public SellerDashboard getDashboard(int sellerId) {
        String key = DASHBOARD_KEY + sellerId;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteDashboard(int sellerId) {
        String key = DASHBOARD_KEY + sellerId;
        redisTemplate.delete(key);
    }

    /**
     * 清除所有物化视图数据，即删除所有以 "sellerDashboard:" 开头的 Redis key
     */
    @Override
    public void clearMaterializedView() {
        Set<String> keys = redisTemplate.keys(DASHBOARD_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 填充物化视图：从数据库加载聚合数据并保存到 Redis。
     * 注意：这里我们使用 loadSellerDashboardsFromDB() 模拟从数据库加载数据，
     * 在实际系统中应将此方法替换为真实的聚合查询逻辑。
     */
    @Override
    public void populateMaterializedView() {
        List<SellerDashboard> dashboards = loadSellerDashboardsFromDB();
        if (dashboards != null) {
            for (SellerDashboard dashboard : dashboards) {
                saveDashboard(dashboard);
            }
        }
    }

    /**
     * 模拟方法：从数据库加载 SellerDashboard 数据。
     * 实际场景中应使用 JDBC、JPA 或其它方式从 seller_order_summary 表中查询数据。
     */
    private List<SellerDashboard> loadSellerDashboardsFromDB() {
        // TODO: 实现从数据库加载 SellerDashboard 数据的逻辑
        return new ArrayList<>();
    }
}


