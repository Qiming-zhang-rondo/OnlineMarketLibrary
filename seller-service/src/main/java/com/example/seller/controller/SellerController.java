package com.example.seller.controller;

import com.example.seller.dto.SellerDashboard;
import com.example.seller.model.Seller;
import com.example.seller.service.SellerService;
import com.example.seller.repository.SellerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
public class SellerController {

    private final SellerRepository sellerRepository;
    private final SellerService sellerService;
    private final Logger logger = LoggerFactory.getLogger(SellerController.class);

    public SellerController(SellerRepository sellerRepository, SellerService sellerService) {
        this.sellerRepository = sellerRepository;
        this.sellerService = sellerService;
    }

    @PostMapping("/")
    public ResponseEntity<Void> addSeller(@RequestBody Seller seller) {
        logger.info("[AddSeller] received for seller {}", seller.getId());
        try {
            sellerRepository.save(seller);
            logger.info("[AddSeller] completed for seller {}", seller.getId());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("[AddSeller] failed for seller {}: {}", seller.getId(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<Seller> getSeller(@PathVariable int sellerId) {
        logger.info("[GetSeller] received for seller {}", sellerId);
        return sellerRepository.findById(sellerId)
            .map(seller -> new ResponseEntity<>(seller, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/dashboard/{sellerId}")
    public ResponseEntity<SellerDashboard> getDashboard(@PathVariable int sellerId) {
        try {
            SellerDashboard dashboard = sellerService.queryDashboard(sellerId);
            return new ResponseEntity<>(dashboard, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[GetDashboard] failed for seller {}: {}", sellerId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/reset")
    public ResponseEntity<Void> reset() {
        logger.warn("Reset requested");
        sellerService.reset();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("/cleanup")
    public ResponseEntity<Void> cleanup() {
        logger.warn("Cleanup requested");
        sellerService.cleanup();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
