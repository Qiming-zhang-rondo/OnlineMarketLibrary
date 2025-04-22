package com.example.stock.config;

public interface IStockConfig {
    boolean isStreaming();
    boolean isInMemoryDb();
    boolean isRaiseStockFailed();
    int getDefaultInventory();
    boolean isPostgresEmbed();
    boolean isLogging();
    int getLoggingDelay();
    String getRamDiskDir();
}
