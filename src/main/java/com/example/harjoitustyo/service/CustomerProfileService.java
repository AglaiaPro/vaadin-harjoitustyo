package com.example.harjoitustyo.service;

import com.example.harjoitustyo.model.CustomerProfile;
import com.example.harjoitustyo.repository.CustomerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerProfileService {

    private final CustomerProfileRepository profileRepository;
    private final HistoryService historyService;

    public CustomerProfileService(CustomerProfileRepository profileRepository, HistoryService historyService) {
        this.profileRepository = profileRepository;
        this.historyService = historyService;
    }

    @Transactional(readOnly = true)
    public List<CustomerProfile> findAll() {
        return profileRepository.findAll();
    }

    @Transactional
    public CustomerProfile save(CustomerProfile profile) {
        boolean created = profile.getId() == null;
        CustomerProfile saved = profileRepository.save(profile);
        historyService.record("CustomerProfile", saved.getId(), created ? "CREATED" : "UPDATED", saved.getCompanyName());
        return saved;
    }

    @Transactional
    public void delete(CustomerProfile profile) {
        Long id = profile.getId();
        String summary = profile.getCompanyName();
        profileRepository.delete(profile);
        historyService.record("CustomerProfile", id, "DELETED", summary);
    }
}
