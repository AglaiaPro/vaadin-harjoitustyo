package com.example.harjoitustyo.service;

import com.example.harjoitustyo.model.HistoryEntry;
import com.example.harjoitustyo.repository.HistoryEntryRepository;
import com.example.harjoitustyo.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class HistoryService {

    private final HistoryEntryRepository historyRepository;

    public HistoryService(HistoryEntryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Transactional
    public void record(String entityName, Long entityId, String action, String summary) {
        if (entityId == null) {
            return;
        }
        HistoryEntry entry = new HistoryEntry();
        entry.setEntityName(entityName);
        entry.setEntityId(entityId);
        entry.setAction(action);
        entry.setSummary(summary);
        entry.setUsername(SecurityUtils.currentUsername().orElse("system"));
        entry.setChangedAt(Instant.now());
        historyRepository.save(entry);
        PushBroadcaster.broadcast(action + " " + entityName + ": " + summary);
    }

    @Transactional(readOnly = true)
    public List<HistoryEntry> latest() {
        return historyRepository.findTop100ByOrderByChangedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<HistoryEntry> forEntity(String entityName, Long entityId) {
        return historyRepository.findByEntityNameAndEntityIdOrderByChangedAtDesc(entityName, entityId);
    }
}
