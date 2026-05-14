package com.example.harjoitustyo.view;

import com.example.harjoitustyo.service.PasswordResetService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("forgot-password")
@PageTitle("Forgot password")
@AnonymousAllowed
public class ForgotPasswordView extends VerticalLayout {

    public ForgotPasswordView(PasswordResetService resetService) {
        addClassName("auth-form-view");
        setMaxWidth("520px");

        EmailField email = new EmailField("Email");
        Paragraph devLink = new Paragraph();
        Button send = new Button("Send reset link");
        send.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        send.addClickListener(event -> resetService.createResetToken(email.getValue()).ifPresentOrElse(url -> {
            devLink.setText("Reset link generated: " + url);
            Notification.show("Reset email sent if SMTP is configured.");
        }, () -> Notification.show("No account was found for that email.")));

        add(new H1("Reset password"), email, send, devLink,
                new Button("Back to login", event -> getUI().ifPresent(ui -> ui.navigate(LoginView.class))));
    }
}
