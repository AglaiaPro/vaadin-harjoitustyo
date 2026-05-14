package com.example.harjoitustyo.view;

import com.example.harjoitustyo.model.Customer;
import com.example.harjoitustyo.model.Project;
import com.example.harjoitustyo.model.ProjectPriority;
import com.example.harjoitustyo.model.ProjectStatus;
import com.example.harjoitustyo.model.Role;
import com.example.harjoitustyo.model.Technology;
import com.example.harjoitustyo.service.CustomerService;
import com.example.harjoitustyo.service.ProjectService;
import com.example.harjoitustyo.service.TechnologyService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "projects", layout = MainLayout.class)
@PageTitle("Projects")
@PermitAll
public class ProjectView extends SplitLayout implements BeforeEnterObserver {

    private final ProjectService projectService;
    private final CustomerService customerService;
    private final TechnologyService technologyService;
    private final Grid<Project> grid = new Grid<>(Project.class, false);
    private final ProjectForm form = new ProjectForm();
    private Project edited;

    public ProjectView(ProjectService projectService, CustomerService customerService, TechnologyService technologyService) {
        this.projectService = projectService;
        this.customerService = customerService;
        this.technologyService = technologyService;
        addClassName("crud-view");
        setSizeFull();

        grid.addColumn(Project::getTitle).setHeader("Title").setAutoWidth(true);
        grid.addColumn(project -> project.getCustomer() == null ? "-" : project.getCustomer().getName())
                .setHeader("1:N customer").setAutoWidth(true);
        grid.addColumn(Project::getStatus).setHeader("Status").setAutoWidth(true);
        grid.addColumn(Project::getPriority).setHeader("Priority").setAutoWidth(true);
        grid.addColumn(Project::getDeadline).setHeader("Deadline").setAutoWidth(true);
        grid.addColumn(Project::getTechnologyNames).setHeader("M:N technologies").setAutoWidth(true);
        grid.asSingleSelect().addValueChangeListener(event -> edit(event.getValue()));

        Button newButton = new Button("New project", event -> edit(new Project()));
        newButton.addClassName("smooth-button");
        VerticalLayout list = new VerticalLayout(new H2("Projects: USER and SUPER area"), newButton, grid);
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
        form.customer.setItems(customerService.findAll());
        form.technologies.setItems(technologyService.findAll());
        grid.setItems(projectService.findAll());
    }

    private void edit(Project project) {
        edited = project;
        form.setVisible(project != null);
        form.binder.readBean(project);
    }

    private void save() {
        try {
            form.binder.writeBean(edited);
            projectService.save(edited);
            refresh();
            edit(null);
            Notification.show("Project saved");
        } catch (ValidationException exception) {
            Notification.show("Check required fields");
        }
    }

    private void delete() {
        if (edited != null && edited.getId() != null) {
            projectService.delete(edited);
            refresh();
            edit(null);
            Notification.show("Project deleted");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        ViewGuards.requireAnyRole(event, Role.USER, Role.SUPER);
    }

    private static class ProjectForm extends FormLayout {
        private final TextField title = new TextField("Title");
        private final TextArea description = new TextArea("Description");
        private final BigDecimalField budget = new BigDecimalField("Budget");
        private final DatePicker startDate = new DatePicker("Start date");
        private final DatePicker deadline = new DatePicker("Deadline");
        private final ComboBox<ProjectStatus> status = new ComboBox<>("Status");
        private final ComboBox<ProjectPriority> priority = new ComboBox<>("Priority");
        private final ComboBox<Customer> customer = new ComboBox<>("Customer");
        private final CheckboxGroup<Technology> technologies = new CheckboxGroup<>("Technologies");
        private final Button save = new Button("Save");
        private final Button delete = new Button("Delete");
        private final Button cancel = new Button("Cancel");
        private final BeanValidationBinder<Project> binder = new BeanValidationBinder<>(Project.class);

        ProjectForm() {
            addClassName("entity-form");
            status.setItems(ProjectStatus.values());
            priority.setItems(ProjectPriority.values());
            customer.setItemLabelGenerator(Customer::getName);
            technologies.setItemLabelGenerator(Technology::getName);
            description.setMinHeight("120px");
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            save.addClassName("smooth-button");
            delete.addClassName("smooth-button");
            binder.bindInstanceFields(this);
            add(title, description, budget, startDate, deadline, status, priority, customer, technologies,
                    new HorizontalLayout(save, delete, cancel));
        }
    }
}
