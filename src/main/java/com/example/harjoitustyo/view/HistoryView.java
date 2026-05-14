package com.example.harjoitustyo.view;

import com.example.harjoitustyo.model.HistoryEntry;
import com.example.harjoitustyo.service.HistoryService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "history", layout = MainLayout.class)
@PageTitle("History")
@PermitAll
public class HistoryView extends VerticalLayout implements BeforeEnterObserver {

    private final HistoryService historyService;

    public HistoryView(HistoryService historyService) {
        this.historyService = historyService;
        addClassName("history-view");
        setWidthFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (ViewGuards.requireLogin(event)) {
            render();
        }
    }

    private void render() {
        removeAll();
        add(new H2("Entity change timeline"));
        for (HistoryEntry entry : historyService.latest()) {
            Div item = new Div();
            item.addClassName("timeline-item");
            Span action = new Span(entry.getAction() + " " + entry.getEntityName() + " #" + entry.getEntityId());
            action.addClassName("timeline-action");
            Span meta = new Span(entry.getChangedAt() + " by " + entry.getUsername());
            Span summary = new Span(entry.getSummary());
            item.add(action, meta, summary);
            add(item);
        }
    }
}
