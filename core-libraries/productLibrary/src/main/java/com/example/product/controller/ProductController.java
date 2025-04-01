package com.example.product.controller;

import com.example.common.events.PriceUpdate;
import com.example.product.model.Product;
import com.example.product.model.ProductId;
import com.example.product.repository.ProductRepository;
import com.example.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{sellerId}")
    public ResponseEntity<List<Product>> getBySellerId(@PathVariable int sellerId) {
        logger.info("[GetBySeller] received for seller {}", sellerId);
        if (sellerId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        List<Product> products = productRepository.findByIdSellerId(sellerId);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{sellerId}/{productId}")
    public ResponseEntity<Product> getBySellerIdAndProductId(@PathVariable int sellerId, @PathVariable int productId) {
        logger.info("[GetBySellerIdAndProductId] received for product {}", productId);
        if (productId <= 0) {
            return ResponseEntity.badRequest().body(null);
        }

        Optional<Product> product = productRepository.findById(new ProductId(sellerId, productId));
        if (!product.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(product.get());
    }

    @PostMapping("/")
    public ResponseEntity<Void> addProduct(@RequestBody com.example.common.entities.Product commonProduct) {
        try {
            Product product = convertToInternalProduct(commonProduct);
            productService.processCreateProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
   


    @PutMapping("/")
    public ResponseEntity<Void> updateProduct(@RequestBody com.example.common.entities.Product commonProduct) {
        Product product = convertToInternalProduct(commonProduct);
        try {
            productService.processProductUpdate(product);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error(e.toString());
            productService.processPoisonProductUpdate(product);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/")
    public ResponseEntity<Void> updateProductPrice(@RequestBody PriceUpdate update) {
        if (update.getVersion() == null) {
            update.setVersion("0");
        }
        logger.info("Received price update request: {}", update);
        try {
            productService.processPriceUpdate(update);
        } catch (Exception e) {
            logger.error(e.toString());
            productService.processPoisonPriceUpdate(update);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/cleanup")
    public ResponseEntity<Void> cleanup() {
        logger.warn("Cleanup requested at {}", System.currentTimeMillis());
        productService.cleanup();
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/reset")
    public ResponseEntity<Void> reset() {
         productService.reset();  
        return ResponseEntity.ok().build();
    }

    private Product convertToInternalProduct(com.example.common.entities.Product commonProduct) {
        Product product = new Product();
        ProductId productId = new ProductId(commonProduct.getSellerId(),commonProduct.getProductId());
        product.setId(productId);
        product.setName(commonProduct.getName());
        product.setSku(commonProduct.getSku());
        product.setCategory(commonProduct.getCategory());
        product.setDescription(commonProduct.getDescription());
        product.setPrice(commonProduct.getPrice());
        product.setFreightValue(commonProduct.getFreightValue());
        product.setStatus(commonProduct.getStatus());
        product.setVersion(commonProduct.getVersion());
        return product;
    }
    
}
