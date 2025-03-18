// package com.example.paymentProvider;

// import com.example.common.integration.Currency;
// import com.example.common.integration.PaymentIntent;
// import com.example.common.integration.PaymentIntentCreateOptions;
// import com.example.paymentProvider.infra.PaymentProviderConfig;
// import com.example.paymentProvider.service.PaymentProvider;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;

// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// @SpringBootTest
// class PaymentProviderServiceTest {

//     private PaymentProviderConfig config;
//     private PaymentProvider paymentProviderService;

//     @BeforeEach
//     void setUp() {
//         // 实例化配置和服务，并注入配置
//         config = new PaymentProviderConfig();
//         paymentProviderService = new PaymentProvider(config);
//     }

//     @Test
//     void processPaymentIntent_ShouldReturnSucceededIntent_WhenSuccess() {
//         // 设置 config 中的失败概率为 0，确保总是成功
//         config.setFailPercentage(0);

//         PaymentIntentCreateOptions options = new PaymentIntentCreateOptions();
//         options.setAmount(100.0f);
//         options.setCustomer("customer123");
//         options.setCurrency(Currency.USD);
//         options.setIdempotencyKey(UUID.randomUUID().toString());

//         PaymentIntent intent = paymentProviderService.processPaymentIntent(options);

//         assertEquals("succeeded", intent.getStatus());
//         assertEquals(100.0f, intent.getAmount());
//         assertEquals("USD", intent.getCurrency());
//         assertEquals("customer123", intent.getCustomer());
//     }

//     @Test
//     void processPaymentIntent_ShouldReturnCanceledIntent_WhenFail() {
//         // 设置失败概率为 100%，确保总是失败
//         config.setFailPercentage(100);

//         PaymentIntentCreateOptions options = new PaymentIntentCreateOptions();
//         options.setAmount(50.0f);
//         options.setCustomer("customer456");
//         options.setCurrency(Currency.USD);
//         options.setIdempotencyKey(UUID.randomUUID().toString());

//         PaymentIntent intent = paymentProviderService.processPaymentIntent(options);

//         assertEquals("canceled", intent.getStatus());
//         assertEquals(50.0f, intent.getAmount());
//         assertEquals("USD", intent.getCurrency());
//         assertEquals("customer456", intent.getCustomer());
//     }

//     @Test
//     void processPaymentIntent_ShouldReturnSameIntent_WhenIdempotencyKeyExists() {
//         PaymentIntentCreateOptions options = new PaymentIntentCreateOptions();
//         options.setAmount(200.0f);
//         options.setCustomer("customer789");
//         options.setCurrency(Currency.USD);
//         String idempotencyKey = UUID.randomUUID().toString();
//         options.setIdempotencyKey(idempotencyKey);

//         // 第一次调用并保存结果
//         PaymentIntent firstIntent = paymentProviderService.processPaymentIntent(options);

//         // 第二次调用相同的 idempotencyKey，应返回相同的结果
//         PaymentIntent secondIntent = paymentProviderService.processPaymentIntent(options);

//         assertEquals(firstIntent, secondIntent);
//         assertTrue(firstIntent == secondIntent);
//     }
// }
