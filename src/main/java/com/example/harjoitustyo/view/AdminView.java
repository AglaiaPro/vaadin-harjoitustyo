package com.example.harjoitustyo.view;

import com.example.harjoitustyo.model.AppUser;
import com.example.harjoitustyo.model.Role;
import com.example.harjoitustyo.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin")
@PermitAll
public class AdminView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;
    private final Grid<AppUser> grid = new Grid<>(AppUser.class, false);
    private final TextField username = new TextField("Username");
    private final TextField fullName = new TextField("Full name");
    private final EmailField email = new EmailField("Email");
    private final ComboBox<Role> role = new ComboBox<>("Role");
    private final PasswordField password = new PasswordField("New password");
    private AppUser edited;

    public AdminView(UserService userService) {
        this.userService = userService;
        addClassName("admin-view");
        setSizeFull();

        grid.addColumn(AppUser::getUsername).setHeader("Username").setAutoWidth(true);
        grid.addColumn(AppUser::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(AppUser::getFullName).setHeader("Full name").setAutoWidth(true);
        grid.addColumn(AppUser::getRole).setHeader("Role").setAutoWidth(true);
        grid.asSingleSelect().addValueChangeListener(event -> edit(event.getValue()));
        role.setItems(Role.values());

        Button save = new Button("Save user", event -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button refresh = new Button("Refresh", event -> refresh());
        HorizontalLayout form = new HorizontalLayout(username, fullName, email, role, password, save, refresh);
        form.setAlignItems(Alignment.BASELINE);

        add(new H2("Admin-only user management"), form, grid);
        expand(grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (ViewGuards.requireAnyRole(event, Role.ADMIN)) {
            refresh();
        }
    }

    private void refresh() {
        grid.setItems(userService.findAll());
    }

    private void edit(AppUser user) {
        edited = user;
        if (user == null) {
            username.clear();
            fullName.clear();
            email.clear();
            role.clear();
            password.clear();
            return;
        }
        username.setValue(user.getUsername());
        username.setReadOnly(true);
        fullName.setValue(user.getFullName());
        email.setValue(user.getEmail());
        role.setValue(user.getRole());
        password.clear();
    }

    private void save() {
        if (edited == null) {
            Notification.show("Select a user first");
            return;
        }
        edited.setFullName(fullName.getValue());
        edited.setEmail(email.getValue());
        edited.setRole(role.getValue());
        userService.save(edited);
        if (!password.getValue().isBlank()) {
            userService.updatePassword(edited, password.getValue());
        }
        refresh();
        Notification.show("User saved");
    }
}
