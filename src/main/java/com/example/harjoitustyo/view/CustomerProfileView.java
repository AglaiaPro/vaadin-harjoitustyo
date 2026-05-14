package com.example.harjoitustyo.view;

import com.example.harjoitustyo.model.Customer;
import com.example.harjoitustyo.model.CustomerProfile;
import com.example.harjoitustyo.service.CustomerProfileService;
import com.example.harjoitustyo.service.CustomerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "profiles", layout = MainLayout.class)
@PageTitle("Customer profiles")
@PermitAll
public class CustomerProfileView extends SplitLayout implements BeforeEnterObserver {

    private final CustomerProfileService profileService;
    private final CustomerService customerService;
    private final Grid<CustomerProfile> grid = new Grid<>(CustomerProfile.class, false);
    private final ProfileForm form = new ProfileForm();
    private CustomerProfile edited;

    public CustomerProfileView(CustomerProfileService profileService, CustomerService customerService) {
        this.profileService = profileService;
        this.customerService = customerService;
        addClassName("crud-view");
        setSizeFull();

        grid.addColumn(CustomerProfile::getCompanyName).setHeader("Company").setAutoWidth(true);
        grid.addColumn(profile -> profile.getCustomer() == null ? "-" : profile.getCustomer().getName())
                .setHeader("1:1 customer").setAutoWidth(true);
        grid.addColumn(CustomerProfile::getVatNumber).setHeader("VAT").setAutoWidth(true);
        grid.addColumn(CustomerProfile::getContactPerson).setHeader("Contact").setAutoWidth(true);
        grid.addColumn(CustomerProfile::getWebsite).setHeader("Website").setAutoWidth(true);
        grid.asSingleSelect().addValueChangeListener(event -> edit(event.getValue()));

        VerticalLayout list = new VerticalLayout(new H2("Customer profiles"), new Button("New profile", event -> edit(new CustomerProfile())), grid);
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
        grid.setItems(profileService.findAll());
    }

    private void edit(CustomerProfile profile) {
        edited = profile;
        form.setVisible(profile != null);
        form.binder.readBean(profile);
    }

    private void save() {
        try {
            form.binder.writeBean(edited);
            profileService.save(edited);
            refresh();
            edit(null);
            Notification.show("Profile saved");
        } catch (ValidationException exception) {
            Notification.show("Check required fields");
        }
    }

    private void delete() {
        if (edited != null && edited.getId() != null) {
            profileService.delete(edited);
            refresh();
            edit(null);
            Notification.show("Profile deleted");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        ViewGuards.requireLogin(event);
    }

    private static class ProfileForm extends FormLayout {
        private final ComboBox<Customer> customer = new ComboBox<>("Customer");
        private final TextField companyName = new TextField("Company name");
        private final TextField vatNumber = new TextField("VAT number");
        private final TextField contactPerson = new TextField("Contact person");
        private final TextField website = new TextField("Website");
        private final TextArea notes = new TextArea("Notes");
        private final DatePicker foundedDate = new DatePicker("Founded date");
        private final Button save = new Button("Save");
        private final Button delete = new Button("Delete");
        private final Button cancel = new Button("Cancel");
        private final BeanValidationBinder<CustomerProfile> binder = new BeanValidationBinder<>(CustomerProfile.class);

        ProfileForm() {
            addClassName("entity-form");
            customer.setItemLabelGenerator(Customer::getName);
            notes.setMinHeight("120px");
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            save.addClassName("smooth-button");
            delete.addClassName("smooth-button");
            binder.bindInstanceFields(this);
            add(customer, companyName, vatNumber, contactPerson, website, notes, foundedDate,
                    new HorizontalLayout(save, delete, cancel));
        }
    }
}
