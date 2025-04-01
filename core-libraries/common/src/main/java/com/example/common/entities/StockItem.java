package com.example.common.entities;

/**
 * Represents a stock item in the inventory.
 */
public class StockItem {

    /**
     * The ID of the seller.
     */
    private int seller_id;

    /**
     * The ID of the product.
     */
    private int product_id;

    /**
     * The quantity of the product available in stock.
     */
    private int qty_available;

    /**
     * The quantity of the product reserved for orders.
     */
    private int qty_reserved;

    /**
     * The count of orders associated with this product.
     */
    private int order_count;

    /**
     * Year-to-date sales volume.
     */
    private int ytd;

    /**
     * Additional data about the stock item, if any.
     */
    private String data;

    /**
     * The version of the stock item, for concurrency control.
     */
    private String version;

    /**
     * Default constructor.
     */
    public StockItem() {
    }

    /**
     * Parameterized constructor to initialize all fields.
     *
     * @param seller_id    The ID of the seller.
     * @param product_id   The ID of the product.
     * @param qty_available The quantity available in stock.
     * @param qty_reserved The quantity reserved for orders.
     * @param order_count  The number of associated orders.
     * @param ytd          Year-to-date sales volume.
     * @param data         Additional data about the stock item.
     * @param version      The version of the stock item.
     */
    public StockItem(int seller_id, int product_id, int qty_available, int qty_reserved, int order_count, int ytd, String data, String version) {
        this.seller_id = seller_id;
        this.product_id = product_id;
        setQty_available(qty_available);
        setQty_reserved(qty_reserved);
        setOrder_count(order_count);
        this.ytd = ytd;
        this.data = data;
        this.version = version;
    }

    // Getters and Setters
    public int getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(int seller_id) {
        this.seller_id = seller_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQty_available() {
        return qty_available;
    }

    public void setQty_available(int qty_available) {
        if (qty_available < 0) {
            throw new IllegalArgumentException("Quantity available cannot be negative.");
        }
        this.qty_available = qty_available;
    }

    public int getQty_reserved() {
        return qty_reserved;
    }

    public void setQty_reserved(int qty_reserved) {
        if (qty_reserved < 0) {
            throw new IllegalArgumentException("Quantity reserved cannot be negative.");
        }
        this.qty_reserved = qty_reserved;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        if (order_count < 0) {
            throw new IllegalArgumentException("Order count cannot be negative.");
        }
        this.order_count = order_count;
    }

    public int getYtd() {
        return ytd;
    }

    public void setYtd(int ytd) {
        this.ytd = ytd;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "StockItem{" +
                "seller_id=" + seller_id +
                ", product_id=" + product_id +
                ", qty_available=" + qty_available +
                ", qty_reserved=" + qty_reserved +
                ", order_count=" + order_count +
                ", ytd=" + ytd +
                ", data='" + data + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
