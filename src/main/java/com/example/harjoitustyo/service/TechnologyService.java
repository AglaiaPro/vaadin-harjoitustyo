package com.example.harjoitustyo.service;

import com.example.harjoitustyo.model.Technology;
import com.example.harjoitustyo.repository.TechnologyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TechnologyService {

    private final TechnologyRepository technologyRepository;
    private final HistoryService historyService;

    public TechnologyService(TechnologyRepository technologyRepository, HistoryService historyService) {
        this.technologyRepository = technologyRepository;
        this.historyService = historyService;
    }

    @Transactional(readOnly = true)
    public List<Technology> findAll() {
        return technologyRepository.findAll();
    }

    @Transactional
    public Technology save(Technology technology) {
        boolean created = technology.getId() == null;
        Technology saved = technologyRepository.save(technology);
        historyService.record("Technology", saved.getId(), created ? "CREATED" : "UPDATED", saved.getName());
        return saved;
    }

    @Transactional
    public void delete(Technology technology) {
        Long id = technology.getId();
        String summary = technology.getName();
        technologyRepository.findById(id).ifPresent(managed -> {
            managed.getProjects().forEach(project -> project.getTechnologies().remove(managed));
            technologyRepository.delete(managed);
            historyService.record("Technology", id, "DELETED", summary);
        });
    }
}
