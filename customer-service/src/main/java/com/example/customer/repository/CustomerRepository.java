package com.example.customer.repository;

import com.example.customer.model.Customer;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {


    Customer findById(int id);


    @Modifying
    @Transactional
    @Query("UPDATE Customer c SET c.successPaymentCount = 0, c.failedPaymentCount = 0")
    void reset();


}
