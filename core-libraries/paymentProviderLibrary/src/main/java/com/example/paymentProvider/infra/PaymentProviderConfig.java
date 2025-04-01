package com.example.paymentProvider.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;

// @Configuration
@ConfigurationProperties(prefix = "paymentprovider")
public class PaymentProviderConfig {
    
    private int failPercentage = 1;  // default fail percentage is 1%

    public int getFailPercentage() {
        return failPercentage;
    }

    public void setFailPercentage(int failPercentage) {
        this.failPercentage = failPercentage;
    }
}
