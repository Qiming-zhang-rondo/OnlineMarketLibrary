package com.example.payment.config;

public interface IPaymentConfig {
    boolean isPaymentProvider();
    String getPaymentProviderUrl();
    boolean isStreaming();
    boolean isInMemoryDb();
    boolean isPostgresEmbed();
    boolean isLogging();
    int getLoggingDelay();
    String getRamDiskDir();
    int getDelay();
}