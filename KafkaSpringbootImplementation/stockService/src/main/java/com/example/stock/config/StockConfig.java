package com.example.stock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "stock")
public class StockConfig implements IStockConfig {

    private boolean streaming = false;
    private boolean inMemoryDb = false;
    private boolean raiseStockFailed = false;
    private int defaultInventory = 10000;
    private boolean postgresEmbed = false;
    private boolean logging = false;
    private int loggingDelay = 10000;
    private String ramDiskDir = "";

    @Override
    public boolean isStreaming() {
        return streaming;
    }

    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }

    @Override
    public boolean isInMemoryDb() {
        return inMemoryDb;
    }

    public void setInMemoryDb(boolean inMemoryDb) {
        this.inMemoryDb = inMemoryDb;
    }

    @Override
    public boolean isRaiseStockFailed() {
        return raiseStockFailed;
    }

    public void setRaiseStockFailed(boolean raiseStockFailed) {
        this.raiseStockFailed = raiseStockFailed;
    }

    @Override
    public int getDefaultInventory() {
        return defaultInventory;
    }

    public void setDefaultInventory(int defaultInventory) {
        this.defaultInventory = defaultInventory;
    }

    @Override
    public boolean isPostgresEmbed() {
        return postgresEmbed;
    }

    public void setPostgresEmbed(boolean postgresEmbed) {
        this.postgresEmbed = postgresEmbed;
    }

    @Override
    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    @Override
    public int getLoggingDelay() {
        return loggingDelay;
    }

    public void setLoggingDelay(int loggingDelay) {
        this.loggingDelay = loggingDelay;
    }

    @Override
    public String getRamDiskDir() {
        return ramDiskDir;
    }

    public void setRamDiskDir(String ramDiskDir) {
        this.ramDiskDir = ramDiskDir;
    }
}
