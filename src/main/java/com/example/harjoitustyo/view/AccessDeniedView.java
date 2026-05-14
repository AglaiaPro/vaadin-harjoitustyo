package com.example.harjoitustyo.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "access-denied", layout = MainLayout.class)
@PageTitle("Access denied")
@AnonymousAllowed
public class AccessDeniedView extends VerticalLayout {

    public AccessDeniedView() {
        addClassName("centered-page");
        H1 title = new H1("Ei kayttooikeutta");
        Paragraph message = new Paragraph("Tama sivu vaatii eri roolin. Kirjaudu toisena kayttajana tai palaa etusivulle.");
        Button home = new Button("Takaisin etusivulle", event -> getUI().ifPresent(ui -> ui.navigate("")));
        home.addClassName("smooth-button");
        add(title, message, home);
    }
}
