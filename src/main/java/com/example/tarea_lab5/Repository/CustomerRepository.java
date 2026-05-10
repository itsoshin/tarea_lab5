package com.example.tarea_lab5.Repository;

import com.example.tarea_lab5.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Integer> {
    Customer findByDocument(String document);
}
