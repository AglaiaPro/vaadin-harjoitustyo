package com.example.harjoitustyo.view;

import com.example.harjoitustyo.service.PasswordResetService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("reset-password")
@PageTitle("Reset password")
@AnonymousAllowed
public class ResetPasswordView extends VerticalLayout implements HasUrlParameter<String> {

    private final PasswordResetService resetService;
    private String token;

    public ResetPasswordView(PasswordResetService resetService) {
        this.resetService = resetService;
        addClassName("auth-form-view");
        setMaxWidth("480px");

        PasswordField password = new PasswordField("New password");
        Button save = new Button("Change password");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            if (resetService.resetPassword(token, password.getValue())) {
                Notification.show("Password changed.");
                getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            } else {
                Notification.show("Reset link is invalid or expired.");
            }
        });
        add(new H1("Choose new password"), password, save);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.token = parameter;
    }
}
