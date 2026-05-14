package com.example.harjoitustyo.view;

import com.example.harjoitustyo.security.SecurityUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;

public class MainLayout extends AppLayout {

    public MainLayout(AuthenticationContext authenticationContext) {
        addClassName("main-layout");
        createHeader(authenticationContext);
        createDrawer();
    }

    private void createHeader(AuthenticationContext authenticationContext) {
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("CRM Harjoitustyo");
        title.addClassName("app-title");

        HorizontalLayout spacer = new HorizontalLayout();
        spacer.setWidthFull();

        Component userArea = createUserArea(authenticationContext);
        HorizontalLayout header = new HorizontalLayout(toggle, title, spacer, userArea);
        header.addClassName("app-header");
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        addToNavbar(header);
    }

    private Component createUserArea(AuthenticationContext authenticationContext) {
        if (!SecurityUtils.isLoggedIn()) {
            Button login = new Button("Login", event -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
            Button register = new Button("Register", event -> getUI().ifPresent(ui -> ui.navigate(RegisterView.class)));
            login.addClassName("smooth-button");
            register.addClassName("smooth-button");
            return new HorizontalLayout(login, register);
        }

        String username = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername)
                .orElse("user");
        Avatar avatar = new Avatar(username);
        Span name = new Span(username);
        Button profile = new Button(new Icon(VaadinIcon.USER), event -> getUI().ifPresent(ui -> ui.navigate(UserProfileView.class)));
        profile.addClassName("icon-button");
        Button logout = new Button("Logout", event -> authenticationContext.logout());
        logout.addClassName("smooth-button");
        HorizontalLayout user = new HorizontalLayout(avatar, name, profile, logout);
        user.setAlignItems(FlexComponent.Alignment.CENTER);
        return user;
    }

    private void createDrawer() {
        VerticalLayout navigation = new VerticalLayout();
        navigation.addClassName("drawer-content");
        navigation.setPadding(false);
        navigation.setSpacing(false);
        navigation.add(
                navItem(VaadinIcon.HOME, "Home", HomeView.class),
                navItem(VaadinIcon.USERS, "Customers", CustomerView.class),
                navItem(VaadinIcon.CLIPBOARD, "Profiles", CustomerProfileView.class),
                navItem(VaadinIcon.TASKS, "Projects", ProjectView.class),
                navItem(VaadinIcon.COG, "Technologies", TechnologyView.class),
                navItem(VaadinIcon.SEARCH, "Advanced search", AdvancedSearchView.class),
                navItem(VaadinIcon.UPLOAD, "Files & CSV", FilesView.class),
                navItem(VaadinIcon.CLOCK, "History", HistoryView.class),
                navItem(VaadinIcon.KEY, "Admin", AdminView.class)
        );

        Footer footer = new Footer();
        footer.addClassName("app-footer");
        footer.add(new Span("Tekija: opiskelija"), new Span("© 2026 CRM Harjoitustyo"),
                new Anchor("https://vaadin.com", "Vaadin"));

        navigation.add(footer);
        addToDrawer(navigation);
    }

    private RouterLink navItem(VaadinIcon icon, String text, Class<? extends Component> view) {
        RouterLink link = new RouterLink(view);
        link.addClassName("nav-link");
        link.add(icon.create(), new Span(text));
        return link;
    }
}
