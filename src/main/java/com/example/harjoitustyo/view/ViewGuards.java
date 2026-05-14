package com.example.harjoitustyo.view;

import com.example.harjoitustyo.model.Role;
import com.example.harjoitustyo.security.SecurityUtils;
import com.vaadin.flow.router.BeforeEnterEvent;

public final class ViewGuards {

    private ViewGuards() {
    }

    public static boolean requireLogin(BeforeEnterEvent event) {
        if (!SecurityUtils.isLoggedIn()) {
            event.forwardTo(LoginView.class);
            return false;
        }
        return true;
    }

    public static boolean requireAnyRole(BeforeEnterEvent event, Role... roles) {
        if (!requireLogin(event)) {
            return false;
        }
        if (!SecurityUtils.hasAnyRole(roles)) {
            event.forwardTo(AccessDeniedView.class);
            return false;
        }
        return true;
    }
}
