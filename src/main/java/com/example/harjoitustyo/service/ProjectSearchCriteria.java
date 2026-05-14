package com.example.harjoitustyo.service;

import com.example.harjoitustyo.model.ProjectPriority;
import com.example.harjoitustyo.model.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectSearchCriteria {

    private String keyword;
    private ProjectStatus status;
    private ProjectPriority priority;
    private LocalDate startFrom;
    private LocalDate startTo;
    private LocalDate deadlineFrom;
    private LocalDate deadlineTo;
    private BigDecimal minBudget;
    private BigDecimal maxBudget;
    private Long customerId;
    private String customerCity;
    private String customerNameOrEmail;
    private String technologyName;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public ProjectPriority getPriority() {
        return priority;
    }

    public void setPriority(ProjectPriority priority) {
        this.priority = priority;
    }

    public LocalDate getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(LocalDate startFrom) {
        this.startFrom = startFrom;
    }

    public LocalDate getStartTo() {
        return startTo;
    }

    public void setStartTo(LocalDate startTo) {
        this.startTo = startTo;
    }

    public LocalDate getDeadlineFrom() {
        return deadlineFrom;
    }

    public void setDeadlineFrom(LocalDate deadlineFrom) {
        this.deadlineFrom = deadlineFrom;
    }

    public LocalDate getDeadlineTo() {
        return deadlineTo;
    }

    public void setDeadlineTo(LocalDate deadlineTo) {
        this.deadlineTo = deadlineTo;
    }

    public BigDecimal getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(BigDecimal minBudget) {
        this.minBudget = minBudget;
    }

    public BigDecimal getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(BigDecimal maxBudget) {
        this.maxBudget = maxBudget;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerNameOrEmail() {
        return customerNameOrEmail;
    }

    public void setCustomerNameOrEmail(String customerNameOrEmail) {
        this.customerNameOrEmail = customerNameOrEmail;
    }

    public String getTechnologyName() {
        return technologyName;
    }

    public void setTechnologyName(String technologyName) {
        this.technologyName = technologyName;
    }
}
