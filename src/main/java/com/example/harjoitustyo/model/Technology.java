package com.example.harjoitustyo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Technology extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 60)
    private String name;

    @NotBlank
    @Size(min = 2, max = 60)
    private String category;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer maturityLevel = 3;

    @NotBlank
    @Size(min = 2, max = 80)
    private String ownerTeam;

    @NotBlank
    @Size(min = 10, max = 500)
    private String description;

    @NotNull
    private Boolean active = true;

    @NotNull
    @PastOrPresent
    private LocalDate introducedDate = LocalDate.now();

    @ManyToMany(mappedBy = "technologies", fetch = FetchType.LAZY)
    private Set<Project> projects = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getMaturityLevel() {
        return maturityLevel;
    }

    public void setMaturityLevel(Integer maturityLevel) {
        this.maturityLevel = maturityLevel;
    }

    public String getOwnerTeam() {
        return ownerTeam;
    }

    public void setOwnerTeam(String ownerTeam) {
        this.ownerTeam = ownerTeam;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getIntroducedDate() {
        return introducedDate;
    }

    public void setIntroducedDate(LocalDate introducedDate) {
        this.introducedDate = introducedDate;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return name == null ? "Technology" : name;
    }
}
