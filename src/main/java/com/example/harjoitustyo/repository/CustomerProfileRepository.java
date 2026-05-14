package com.example.harjoitustyo.repository;

import com.example.harjoitustyo.model.CustomerProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {

    @Override
    @EntityGraph(attributePaths = "customer")
    List<CustomerProfile> findAll();
}
