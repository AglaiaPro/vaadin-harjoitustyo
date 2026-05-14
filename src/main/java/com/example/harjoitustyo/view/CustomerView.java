package com.example.harjoitustyo.view;

import com.example.harjoitustyo.model.Customer;
import com.example.harjoitustyo.model.CustomerStatus;
import com.example.harjoitustyo.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "customers", layout = MainLayout.class)
@PageTitle("Customers")
@PermitAll
public class CustomerView extends SplitLayout implements BeforeEnterObserver {

    private final CustomerService customerService;
    private final Grid<Customer> grid = new Grid<>(Customer.class, false);
    private final CustomerForm form = new CustomerForm();
    private Customer edited;

    public CustomerView(CustomerService customerService) {
        this.customerService = customerService;
        addClassName("crud-view");
        setSizeFull();

        grid.addColumn(Customer::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Customer::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Customer::getPhone).setHeader("Phone").setAutoWidth(true);
        grid.addColumn(Customer::getCity).setHeader("City").setAutoWidth(true);
        grid.addColumn(Customer::getStatus).setHeader("Status").setAutoWidth(true);
        grid.addColumn(customer -> customer.getProfile() == null ? "-" : customer.getProfile().getCompanyName())
                .setHeader("1:1 profile").setAutoWidth(true);
        grid.addColumn(customer -> customer.getProjects().size()).setHeader("1:N projects").setAutoWidth(true);
        grid.asSingleSelect().addValueChangeListener(event -> edit(event.getValue()));

        VerticalLayout list = new VerticalLayout(new H2("Customers"), new Button("New customer", event -> edit(new Customer())), grid);
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
        grid.setItems(customerService.findAll());
    }

    private void edit(Customer customer) {
        edited = customer;
        form.setVisible(customer != null);
        form.binder.readBean(customer);
    }

    private void save() {
        try {
            form.binder.writeBean(edited);
            customerService.save(edited);
            refresh();
            edit(null);
            Notification.show("Customer saved");
        } catch (ValidationException exception) {
            Notification.show("Check required fields");
        }
    }

    private void delete() {
        if (edited != null && edited.getId() != null) {
            customerService.delete(edited);
            refresh();
            edit(null);
            Notification.show("Customer deleted");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        ViewGuards.requireLogin(event);
    }

    private static class CustomerForm extends FormLayout {
        private final TextField name = new TextField("Name");
        private final EmailField email = new EmailField("Email");
        private final TextField phone = new TextField("Phone");
        private final TextField city = new TextField("City");
        private final Select<CustomerStatus> status = new Select<>();
        private final DatePicker joinedDate = new DatePicker("Joined date");
        private final Button save = new Button("Save");
        private final Button delete = new Button("Delete");
        private final Button cancel = new Button("Cancel");
        private final BeanValidationBinder<Customer> binder = new BeanValidationBinder<>(Customer.class);

        CustomerForm() {
            addClassName("entity-form");
            status.setLabel("Status");
            status.setItems(CustomerStatus.values());
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            save.addClassName("smooth-button");
            delete.addClassName("smooth-button");
            binder.bindInstanceFields(this);
            add(name, email, phone, city, status, joinedDate, new HorizontalLayout(save, delete, cancel));
        }
    }
}
