package com.example.harjoitustyo.view;

import com.example.harjoitustyo.model.Customer;
import com.example.harjoitustyo.model.Project;
import com.example.harjoitustyo.model.ProjectPriority;
import com.example.harjoitustyo.model.ProjectStatus;
import com.example.harjoitustyo.service.CustomerService;
import com.example.harjoitustyo.service.ProjectSearchCriteria;
import com.example.harjoitustyo.service.ProjectSearchService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@Route(value = "advanced-search", layout = MainLayout.class)
@PageTitle("Advanced search")
@CssImport("./styles/search-view.css")
@PermitAll
public class AdvancedSearchView extends VerticalLayout implements BeforeEnterObserver {

    private final ProjectSearchService searchService;
    private final Grid<Project> grid = new Grid<>(Project.class, false);

    private final TextField keyword = new TextField("Keyword");
    private final ComboBox<ProjectStatus> status = new ComboBox<>("Status");
    private final ComboBox<ProjectPriority> priority = new ComboBox<>("Priority");
    private final DatePicker startFrom = new DatePicker("Start from");
    private final DatePicker startTo = new DatePicker("Start to");
    private final DatePicker deadlineFrom = new DatePicker("Deadline from");
    private final DatePicker deadlineTo = new DatePicker("Deadline to");
    private final BigDecimalField minBudget = new BigDecimalField("Min budget");
    private final BigDecimalField maxBudget = new BigDecimalField("Max budget");
    private final ComboBox<Customer> customer = new ComboBox<>("Customer JOIN");
    private final TextField customerCity = new TextField("Customer city");
    private final TextField customerNameOrEmail = new TextField("Customer name/email");
    private final TextField technologyName = new TextField("Technology JOIN");

    public AdvancedSearchView(ProjectSearchService searchService, CustomerService customerService) {
        this.searchService = searchService;
        addClassName("advanced-search-view");
        setSizeFull();

        Span badge = new Span("Criteria API");
        badge.addClassName("criteria-badge");
        badge.getStyle().set("background", "var(--lumo-primary-color-10pct)")
                .set("color", "var(--lumo-primary-text-color)")
                .set("border-radius", "999px")
                .set("padding", "0.25rem 0.75rem");

        status.setItems(ProjectStatus.values());
        priority.setItems(ProjectPriority.values());
        customer.setItems(customerService.findAll());
        customer.setItemLabelGenerator(Customer::getName);

        FormLayout filters = new FormLayout(keyword, status, priority, startFrom, startTo, deadlineFrom, deadlineTo,
                minBudget, maxBudget, customer, customerCity, customerNameOrEmail, technologyName);
        filters.addClassNames("search-filters", LumoUtility.Padding.MEDIUM, LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.BoxShadow.SMALL, LumoUtility.Background.CONTRAST_5);
        filters.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("720px", 3));

        Button search = new Button("Search", event -> runSearch());
        search.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        search.addClassName("smooth-button");
        Button clear = new Button("Clear", event -> clear());

        grid.addColumn(Project::getTitle).setHeader("Title").setAutoWidth(true);
        grid.addColumn(project -> project.getCustomer() == null ? "-" : project.getCustomer().getName()).setHeader("Customer").setAutoWidth(true);
        grid.addColumn(Project::getStatus).setHeader("Status").setAutoWidth(true);
        grid.addColumn(Project::getPriority).setHeader("Priority").setAutoWidth(true);
        grid.addColumn(Project::getBudget).setHeader("Budget").setAutoWidth(true);
        grid.addColumn(Project::getStartDate).setHeader("Start").setAutoWidth(true);
        grid.addColumn(Project::getDeadline).setHeader("Deadline").setAutoWidth(true);
        grid.addColumn(Project::getTechnologyNames).setHeader("Technologies").setAutoWidth(true);
        grid.addClassName("search-grid");

        HorizontalLayout heading = new HorizontalLayout(new H2("Advanced project search"), badge);
        heading.setAlignItems(Alignment.CENTER);
        add(heading, filters, new HorizontalLayout(search, clear), grid);
        expand(grid);
        runSearch();
    }

    private void runSearch() {
        ProjectSearchCriteria criteria = new ProjectSearchCriteria();
        criteria.setKeyword(keyword.getValue());
        criteria.setStatus(status.getValue());
        criteria.setPriority(priority.getValue());
        criteria.setStartFrom(startFrom.getValue());
        criteria.setStartTo(startTo.getValue());
        criteria.setDeadlineFrom(deadlineFrom.getValue());
        criteria.setDeadlineTo(deadlineTo.getValue());
        criteria.setMinBudget(minBudget.getValue());
        criteria.setMaxBudget(maxBudget.getValue());
        criteria.setCustomerId(customer.getValue() == null ? null : customer.getValue().getId());
        criteria.setCustomerCity(customerCity.getValue());
        criteria.setCustomerNameOrEmail(customerNameOrEmail.getValue());
        criteria.setTechnologyName(technologyName.getValue());
        grid.setItems(searchService.search(criteria));
    }

    private void clear() {
        keyword.clear();
        status.clear();
        priority.clear();
        startFrom.clear();
        startTo.clear();
        deadlineFrom.clear();
        deadlineTo.clear();
        minBudget.clear();
        maxBudget.clear();
        customer.clear();
        customerCity.clear();
        customerNameOrEmail.clear();
        technologyName.clear();
        runSearch();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        ViewGuards.requireLogin(event);
    }
}
