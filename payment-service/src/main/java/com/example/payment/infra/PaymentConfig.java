package com.example.payment.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {

    private boolean paymentProvider = false;
    private String paymentProviderUrl = "";
    private boolean streaming = false;
    private boolean inMemoryDb = false;
    private boolean postgresEmbed = false;
    private boolean logging = false;
    private int loggingDelay = 10000;
    private String ramDiskDir = "";
    private int delay = 0;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    // Getters and Setters
    public boolean isPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(boolean paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getPaymentProviderUrl() {
        return paymentProviderUrl;
    }

    public void setPaymentProviderUrl(String paymentProviderUrl) {
        this.paymentProviderUrl = paymentProviderUrl;
    }

    public boolean isStreaming() {
        return streaming;
    }

    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }

    public boolean isInMemoryDb() {
        return inMemoryDb;
    }

    public void setInMemoryDb(boolean inMemoryDb) {
        this.inMemoryDb = inMemoryDb;
    }

    public boolean isPostgresEmbed() {
        return postgresEmbed;
    }

    public void setPostgresEmbed(boolean postgresEmbed) {
        this.postgresEmbed = postgresEmbed;
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

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
