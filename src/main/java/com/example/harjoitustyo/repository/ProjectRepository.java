package com.example.harjoitustyo.repository;

import com.example.harjoitustyo.model.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Override
    @EntityGraph(attributePaths = {"customer", "technologies"})
    List<Project> findAll();
}
