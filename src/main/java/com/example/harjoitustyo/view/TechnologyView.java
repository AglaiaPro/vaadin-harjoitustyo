package com.example.harjoitustyo.view;

import com.example.harjoitustyo.model.Technology;
import com.example.harjoitustyo.service.TechnologyService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "technologies", layout = MainLayout.class)
@PageTitle("Technologies")
@PermitAll
public class TechnologyView extends SplitLayout implements BeforeEnterObserver {

    private final TechnologyService technologyService;
    private final Grid<Technology> grid = new Grid<>(Technology.class, false);
    private final TechnologyForm form = new TechnologyForm();
    private Technology edited;

    public TechnologyView(TechnologyService technologyService) {
        this.technologyService = technologyService;
        addClassName("crud-view");
        setSizeFull();

        grid.addColumn(Technology::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Technology::getCategory).setHeader("Category").setAutoWidth(true);
        grid.addColumn(Technology::getMaturityLevel).setHeader("Maturity").setAutoWidth(true);
        grid.addColumn(Technology::getOwnerTeam).setHeader("Owner").setAutoWidth(true);
        grid.addColumn(technology -> technology.getProjects().size()).setHeader("M:N project count").setAutoWidth(true);
        grid.asSingleSelect().addValueChangeListener(event -> edit(event.getValue()));

        VerticalLayout list = new VerticalLayout(new H2("Technologies"), new Button("New technology", event -> edit(new Technology())), grid);
        list.setSizeFull();
        list.setPadding(true);
        list.expand(grid);

        form.save.addClickListener(event -> save());
        form.delete.addClickListener(event -> delete());
        form.cancel.addClickListener(event -> edit(null));

        addToPrimary(list);
        addToSecondary(form);
        refresh();
        edit(null);
    }

    private void refresh() {
        grid.setItems(technologyService.findAll());
    }

    private void edit(Technology technology) {
        edited = technology;
        form.setVisible(technology != null);
        form.binder.readBean(technology);
    }

    private void save() {
        try {
            form.binder.writeBean(edited);
            technologyService.save(edited);
            refresh();
            edit(null);
            Notification.show("Technology saved");
        } catch (ValidationException exception) {
            Notification.show("Check required fields");
        }
    }

    private void delete() {
        if (edited != null && edited.getId() != null) {
            technologyService.delete(edited);
            refresh();
            edit(null);
            Notification.show("Technology deleted");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        ViewGuards.requireLogin(event);
    }

    private static class TechnologyForm extends FormLayout {
        private final TextField name = new TextField("Name");
        private final TextField category = new TextField("Category");
        private final IntegerField maturityLevel = new IntegerField("Maturity level 1-5");
        private final TextField ownerTeam = new TextField("Owner team");
        private final TextArea description = new TextArea("Description");
        private final Checkbox active = new Checkbox("Active");
        private final DatePicker introducedDate = new DatePicker("Introduced date");
        private final Button save = new Button("Save");
        private final Button delete = new Button("Delete");
        private final Button cancel = new Button("Cancel");
        private final BeanValidationBinder<Technology> binder = new BeanValidationBinder<>(Technology.class);

        TechnologyForm() {
            addClassName("entity-form");
            maturityLevel.setMin(1);
            maturityLevel.setMax(5);
            description.setMinHeight("120px");
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            save.addClassName("smooth-button");
            delete.addClassName("smooth-button");
            binder.bindInstanceFields(this);
            add(name, category, maturityLevel, ownerTeam, description, active, introducedDate,
                    new HorizontalLayout(save, delete, cancel));
        }
    }
}
