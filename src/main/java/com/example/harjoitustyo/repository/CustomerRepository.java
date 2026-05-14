package com.example.harjoitustyo.repository;

import com.example.harjoitustyo.model.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Override
    @EntityGraph(attributePaths = {"profile", "projects"})
    List<Customer> findAll();
}
