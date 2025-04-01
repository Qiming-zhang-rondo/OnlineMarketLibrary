// package com.example.cart.eventMessaging;

// import com.example.common.events.ProductUpdated;
// import com.example.common.events.PriceUpdate;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.springframework.stereotype.Service;

// @Service
// public class JsonCartConsumer extends AbstractCartConsumer {

//     private static final ObjectMapper objectMapper = new ObjectMapper();

//     @Override
//     protected PriceUpdate deserializePriceUpdate(String payload) {
//         try {
//             return objectMapper.readValue(payload, PriceUpdate.class);
//         } catch (Exception e) {
//             throw new RuntimeException("JSON deserialization failed for PriceUpdate", e);
//         }
//     }

//     @Override
//     protected ProductUpdated deserializeProductUpdated(String payload) {
//         try {
//             return objectMapper.readValue(payload, ProductUpdated.class);
//         } catch (Exception e) {
//             throw new RuntimeException("JSON deserialization failed for ProductUpdated", e);
//         }
//     }
// }