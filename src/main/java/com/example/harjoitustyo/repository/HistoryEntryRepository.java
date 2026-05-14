package com.example.harjoitustyo.repository;

import com.example.harjoitustyo.model.HistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryEntryRepository extends JpaRepository<HistoryEntry, Long> {
    List<HistoryEntry> findTop100ByOrderByChangedAtDesc();

    List<HistoryEntry> findByEntityNameAndEntityIdOrderByChangedAtDesc(String entityName, Long entityId);
}
