package com.example.harjoitustyo.view;

import com.example.harjoitustyo.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("register")
@PageTitle("Register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    public RegisterView(UserService userService) {
        addClassName("auth-form-view");
        setMaxWidth("480px");

        TextField username = new TextField("Username");
        EmailField email = new EmailField("Email");
        TextField fullName = new TextField("Full name");
        PasswordField password = new PasswordField("Password");
        Button save = new Button("Create account");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClassName("smooth-button");
        save.addClickListener(event -> {
            try {
                userService.register(username.getValue(), email.getValue(), fullName.getValue(), password.getValue());
                Notification.show("Account created. Admin was notified by email.");
                getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            } catch (Exception exception) {
                Notification.show(exception.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });

        Button back = new Button("Back to login", event -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));

        add(new H1("Register"), username, email, fullName, password, save, back);
    }
}
