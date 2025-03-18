package com.example.cart.service;

import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import com.example.cart.model.ProductReplica;
import com.example.common.driver.MarkStatus;
import com.example.common.events.PriceUpdate;
import com.example.common.events.ProductUpdated;
import com.example.common.requests.CustomerCheckout;


public interface ICartService {


    
    void removeItem(int customerId, int productId, int sellerId);


   
    void seal(Cart cart, boolean cleanItems);

   
    void notifyCheckout(CustomerCheckout customerCheckout);

    
    void cleanCart();


    Cart getCart(int customerId);

 
    void processProductUpdated(ProductReplica productUpdated);

  
    void processPriceUpdate(PriceUpdate priceUpdate);

 
    void reset();

   
    void processPoisonProductUpdated(ProductUpdated productUpdated);


    void processPoisonPriceUpdate(PriceUpdate update);

   
    void processPoisonCheckout(CustomerCheckout customerCheckout, MarkStatus status);
}
