package com.example.harjoitustyo.repository;

import com.example.harjoitustyo.model.Technology;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnologyRepository extends JpaRepository<Technology, Long> {

    @Override
    @EntityGraph(attributePaths = "projects")
    List<Technology> findAll();
}
