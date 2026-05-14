package com.example.harjoitustyo.service;

import com.example.harjoitustyo.model.Project;
import com.example.harjoitustyo.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final HistoryService historyService;

    public ProjectService(ProjectRepository projectRepository, HistoryService historyService) {
        this.projectRepository = projectRepository;
        this.historyService = historyService;
    }

    @Transactional(readOnly = true)
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Transactional
    public Project save(Project project) {
        boolean created = project.getId() == null;
        Project saved = projectRepository.save(project);
        historyService.record("Project", saved.getId(), created ? "CREATED" : "UPDATED", saved.getTitle());
        return saved;
    }

    @Transactional
    public void delete(Project project) {
        Long id = project.getId();
        String summary = project.getTitle();
        projectRepository.findById(id).ifPresent(managed -> {
            managed.getTechnologies().clear();
            projectRepository.delete(managed);
            historyService.record("Project", id, "DELETED", summary);
        });
    }
}
