package com.example.paymentProvider;

import com.example.common.integration.Currency;
import com.example.common.integration.PaymentIntent;
import com.example.common.integration.PaymentIntentCreateOptions;
import com.example.paymentProvider.service.PaymentProviderCore;
import com.example.paymentProvider.config.PaymentProviderConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentProviderServiceTest {

    private PaymentProviderConfig config;
    private PaymentProviderCore paymentProvider;

    @BeforeEach
    void setUp() {
        config = new PaymentProviderConfig();
        paymentProvider = new PaymentProviderCore(config);
    }

    @Test
    void processPaymentIntent_ShouldReturnSucceededIntent_WhenSuccess() {
        config.setFailPercentage(0);

        PaymentIntentCreateOptions options = new PaymentIntentCreateOptions();
        options.setAmount(100.0f);
        options.setCustomer("customer123");
        options.setCurrency(Currency.USD);
        options.setIdempotencyKey(UUID.randomUUID().toString());

        PaymentIntent intent = paymentProvider.processPaymentIntent(options);

        assertEquals("succeeded", intent.getStatus());
        assertEquals(100.0f, intent.getAmount());
        assertEquals("USD", intent.getCurrency());
        assertEquals("customer123", intent.getCustomer());
    }

    @Test
    void processPaymentIntent_ShouldReturnCanceledIntent_WhenFail() {
        config.setFailPercentage(100);

        PaymentIntentCreateOptions options = new PaymentIntentCreateOptions();
        options.setAmount(50.0f);
        options.setCustomer("customer456");
        options.setCurrency(Currency.USD);
        options.setIdempotencyKey(UUID.randomUUID().toString());

        PaymentIntent intent = paymentProvider.processPaymentIntent(options);

        assertEquals("canceled", intent.getStatus());
        assertEquals(50.0f, intent.getAmount());
        assertEquals("USD", intent.getCurrency());
        assertEquals("customer456", intent.getCustomer());
    }

    @Test
    void processPaymentIntent_ShouldReturnSameIntent_WhenIdempotencyKeyExists() {
        String idempotencyKey = UUID.randomUUID().toString();

        PaymentIntentCreateOptions options = new PaymentIntentCreateOptions();
        options.setAmount(200.0f);
        options.setCustomer("customer789");
        options.setCurrency(Currency.USD);
        options.setIdempotencyKey(idempotencyKey);

        PaymentIntent firstIntent = paymentProvider.processPaymentIntent(options);
        PaymentIntent secondIntent = paymentProvider.processPaymentIntent(options);

        assertEquals(firstIntent, secondIntent);
        assertSame(firstIntent, secondIntent); // 更推荐用 assertSame
    }
}