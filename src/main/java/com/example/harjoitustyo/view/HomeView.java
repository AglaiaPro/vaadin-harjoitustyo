package com.example.harjoitustyo.view;

import com.example.harjoitustyo.service.CustomerService;
import com.example.harjoitustyo.service.ProjectService;
import com.example.harjoitustyo.service.PushBroadcaster;
import com.example.harjoitustyo.service.TechnologyService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Map;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Home")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private final H1 title = new H1();
    private final Paragraph lead = new Paragraph();
    private final Div liveFeed = new Div();
    private PushBroadcaster.Registration registration;

    public HomeView(CustomerService customerService, ProjectService projectService, TechnologyService technologyService) {
        addClassName("home-view");
        setSizeFull();

        ComboBox<String> language = new ComboBox<>("Language");
        language.setItems("Suomi", "English");
        language.setValue("Suomi");
        language.addValueChangeListener(event -> applyLanguage(event.getValue()));
        language.getStyle().set("max-width", "180px");

        HorizontalLayout metrics = new HorizontalLayout(
                metricCard("Customers", String.valueOf(customerService.findAll().size())),
                metricCard("Projects", String.valueOf(projectService.findAll().size())),
                metricCard("Technologies", String.valueOf(technologyService.findAll().size()))
        );
        metrics.addClassNames(LumoUtility.Display.FLEX, LumoUtility.Gap.MEDIUM, LumoUtility.Width.FULL);
        metrics.setWidthFull();

        liveFeed.addClassNames("live-feed", LumoUtility.Background.CONTRAST_5, LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM, LumoUtility.BoxShadow.SMALL, LumoUtility.Width.FULL);
        liveFeed.add(new H2("Server Push"), new Paragraph("Waiting for changes..."));

        add(language, title, lead, metrics, liveFeed);
        applyLanguage("Suomi");
    }

    private Div metricCard(String label, String value) {
        Div card = new Div();
        card.addClassNames("metric-card", LumoUtility.Padding.LARGE, LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.BoxShadow.MEDIUM, LumoUtility.Width.FULL, LumoUtility.Background.CONTRAST_5);
        Span number = new Span(value);
        number.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.TextColor.PRIMARY);
        Span text = new Span(label);
        text.addClassNames(LumoUtility.TextColor.SECONDARY);
        card.add(number, text);
        return card;
    }

    private void applyLanguage(String language) {
        Map<String, String> fi = Map.of(
                "title", "Tervetuloa CRM Harjoitustyohon",
                "lead", "Tama paanakymä on julkinen. Kirjaudu sisaan hallitaksesi asiakkaita, projekteja, tiedostoja ja historiaa."
        );
        Map<String, String> en = Map.of(
                "title", "Welcome to the CRM course project",
                "lead", "This home view is public. Sign in to manage customers, projects, files and history."
        );
        Map<String, String> texts = "English".equals(language) ? en : fi;
        title.setText(texts.get("title"));
        lead.setText(texts.get("lead"));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        registration = PushBroadcaster.register(message -> ui.access(() -> {
            liveFeed.removeAll();
            liveFeed.add(new H2("Server Push"), new Paragraph(message));
        }));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (registration != null) {
            registration.remove();
        }
    }
}
