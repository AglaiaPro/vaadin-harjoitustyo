package com.example.harjoitustyo.service;

import com.example.harjoitustyo.model.Customer;
import com.example.harjoitustyo.model.Project;
import com.example.harjoitustyo.model.Technology;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectSearchService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Project> search(ProjectSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Project> query = cb.createQuery(Project.class);
        Root<Project> project = query.from(Project.class);
        project.fetch("customer", JoinType.LEFT);
        project.fetch("technologies", JoinType.LEFT);

        Join<Project, Customer> customer = project.join("customer", JoinType.LEFT);
        Join<Project, Technology> technology = project.join("technologies", JoinType.LEFT);
        List<Predicate> predicates = new ArrayList<>();

        if (hasText(criteria.getKeyword())) {
            String like = like(criteria.getKeyword());
            Predicate title = cb.like(cb.lower(project.get("title")), like);
            Predicate description = cb.like(cb.lower(project.get("description")), like);
            Predicate customerEmail = cb.like(cb.lower(customer.get("email")), like);
            predicates.add(cb.or(title, description, customerEmail));
        }

        if (criteria.getStatus() != null) {
            predicates.add(cb.equal(project.get("status"), criteria.getStatus()));
        }

        if (criteria.getPriority() != null) {
            predicates.add(cb.equal(project.get("priority"), criteria.getPriority()));
        }

        if (criteria.getStartFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(project.get("startDate"), criteria.getStartFrom()));
        }

        if (criteria.getStartTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(project.get("startDate"), criteria.getStartTo()));
        }

        if (criteria.getDeadlineFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(project.get("deadline"), criteria.getDeadlineFrom()));
        }

        if (criteria.getDeadlineTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(project.get("deadline"), criteria.getDeadlineTo()));
        }

        if (criteria.getMinBudget() != null) {
            predicates.add(cb.greaterThanOrEqualTo(project.get("budget"), criteria.getMinBudget()));
        }

        if (criteria.getMaxBudget() != null) {
            predicates.add(cb.lessThanOrEqualTo(project.get("budget"), criteria.getMaxBudget()));
        }

        if (criteria.getCustomerId() != null) {
            predicates.add(cb.equal(customer.get("id"), criteria.getCustomerId()));
        }

        if (hasText(criteria.getCustomerCity())) {
            predicates.add(cb.equal(cb.lower(customer.get("city")), criteria.getCustomerCity().trim().toLowerCase()));
        }

        if (hasText(criteria.getCustomerNameOrEmail())) {
            String like = like(criteria.getCustomerNameOrEmail());
            predicates.add(cb.or(
                    cb.like(cb.lower(customer.get("name")), like),
                    cb.like(cb.lower(customer.get("email")), like)
            ));
        }

        if (hasText(criteria.getTechnologyName())) {
            predicates.add(cb.like(cb.lower(technology.get("name")), like(criteria.getTechnologyName())));
        }

        query.select(project).distinct(true);
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(Predicate[]::new)));
        }
        query.orderBy(cb.asc(project.get("deadline")));

        TypedQuery<Project> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    private boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    private String like(String text) {
        return "%" + text.trim().toLowerCase() + "%";
    }
}
