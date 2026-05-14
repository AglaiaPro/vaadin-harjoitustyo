package com.example.harjoitustyo.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Route("login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView(ObjectProvider<ClientRegistrationRepository> clientRegistrations) {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("CRM Harjoitustyo");
        Paragraph hint = new Paragraph("Kirjaudu sisaan tai rekisteroidy. Demo: admin/admin123, super/super123, user/user123.");

        loginForm.setAction("login");

        Button register = new Button("Rekisteroidy", event -> UI.getCurrent().navigate(RegisterView.class));
        Button forgot = new Button("Unohtuiko salasana?", event -> UI.getCurrent().navigate(ForgotPasswordView.class));
        register.addClassName("smooth-button");
        forgot.addClassName("smooth-button");

        HorizontalLayout oauth = new HorizontalLayout(
                oauthButton(clientRegistrations, "github", "GitHub"),
                oauthButton(clientRegistrations, "google", "Google")
        );
        oauth.setAlignItems(Alignment.CENTER);

        add(title, hint, loginForm, new HorizontalLayout(register, forgot), oauth);
    }

    private Component oauthButton(ObjectProvider<ClientRegistrationRepository> provider,
                                  String registrationId,
                                  String label) {
        if (hasRegistration(provider, registrationId)) {
            Anchor link = new Anchor("/oauth2/authorization/" + registrationId, label);
            link.addClassName("oauth-link");
            return link;
        }
        Button disabled = new Button(label);
        disabled.addClassName("oauth-link");
        disabled.setEnabled(false);
        return disabled;
    }

    private boolean hasRegistration(ObjectProvider<ClientRegistrationRepository> provider, String registrationId) {
        ClientRegistrationRepository repository = provider.getIfAvailable();
        return repository != null && repository.findByRegistrationId(registrationId) != null;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
            Notification.show("Kirjautuminen epaonnistui");
        }
    }
}
