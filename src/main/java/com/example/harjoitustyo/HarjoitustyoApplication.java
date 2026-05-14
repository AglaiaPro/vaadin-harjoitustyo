package com.example.harjoitustyo;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Push
@PWA(name = "CRM Harjoitustyo", shortName = "CRM")
@Theme("harjoitustyo")
@SpringBootApplication
public class HarjoitustyoApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(HarjoitustyoApplication.class, args);
    }
}
