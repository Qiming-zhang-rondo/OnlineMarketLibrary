package com.example.seller.dto;

import com.example.seller.model.OrderEntry;
import com.example.seller.model.OrderSellerView;
import java.util.List;

public class SellerDashboard {

    private OrderSellerView sellerView;   // aggregation view
    private List<OrderEntry> orderEntries;  // details entries

    
    public SellerDashboard(){}

    public SellerDashboard(OrderSellerView sellerView, List<OrderEntry> orderEntries) {
        this.sellerView = sellerView;
        this.orderEntries = orderEntries;
    }

    // Getter 
    public OrderSellerView getSellerView() {
        return sellerView;
    }

    public List<OrderEntry> getOrderEntries() {
        return orderEntries;
    }

    // Setter 
    public void setSellerView(OrderSellerView sellerView) {
        this.sellerView = sellerView;
    }

    public void setOrderEntries(List<OrderEntry> orderEntries) {
        this.orderEntries = orderEntries;
    }
}
