package com.example.seller.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "seller")
public class SellerConfig {

    private boolean postgresEmbed = false;
    private boolean inMemoryDb = false;
    private boolean logging = false;
    private int loggingDelay = 10000;
    private String ramDiskDir = "";

    // Getters and Setters
    public boolean isPostgresEmbed() {
        return postgresEmbed;
    }

    public void setPostgresEmbed(boolean postgresEmbed) {
        this.postgresEmbed = postgresEmbed;
    }

    public boolean isInMemoryDb() {
        return inMemoryDb;
    }

    public void setInMemoryDb(boolean inMemoryDb) {
        this.inMemoryDb = inMemoryDb;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public int getLoggingDelay() {
        return loggingDelay;
    }

    public void setLoggingDelay(int loggingDelay) {
        this.loggingDelay = loggingDelay;
    }

    public String getRamDiskDir() {
        return ramDiskDir;
    }

    public void setRamDiskDir(String ramDiskDir) {
        this.ramDiskDir = ramDiskDir;
    }
}
