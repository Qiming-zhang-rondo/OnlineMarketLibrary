package com.example.order.repository;

import com.example.order.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {

  
    CustomerOrder findByCustomerId(int customerId);
}
