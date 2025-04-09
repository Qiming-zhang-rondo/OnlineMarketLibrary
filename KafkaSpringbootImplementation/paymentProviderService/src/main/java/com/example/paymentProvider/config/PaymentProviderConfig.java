package com.example.paymentProvider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import com.example.paymentProvider.config.IPaymentProviderConfig;

// @Configuration
@ConfigurationProperties(prefix = "paymentprovider")
public class PaymentProviderConfig implements IPaymentProviderConfig{
    
    private int failPercentage = 1;  // default fail percentage is 1%
    @Override
    public int getFailPercentage() {
        return failPercentage;
    }
    @Override
    public void setFailPercentage(int failPercentage) {
        this.failPercentage = failPercentage;
    }
}
