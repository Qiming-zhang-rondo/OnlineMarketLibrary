package com.example.cart.model;

import java.util.List;

public class ReserveInventory {

    private int customerId;
    private List<ReserveInventoryItem> items;

    // Getters and Setters for customerId and items
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public List<ReserveInventoryItem> getItems() {
        return items;
    }

    public void setItems(List<ReserveInventoryItem> items) {
        this.items = items;
    }

    public static class ReserveInventoryItem {
        private int sellerId;
        private int productId;
        private int quantity;

        public ReserveInventoryItem(int sellerId, int productId, int quantity) {
            this.sellerId = sellerId;
            this.productId = productId;
            this.quantity = quantity;
        }

        // Getter and Setter for sellerId
        public int getSellerId() {
            return sellerId;
        }

        public void setSellerId(int sellerId) {
            this.sellerId = sellerId;
        }

        // Getter and Setter for productId
        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        // Getter and Setter for quantity
        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
