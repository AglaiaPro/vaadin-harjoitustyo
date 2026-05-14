package com.example.harjoitustyo.view;

import com.example.harjoitustyo.model.AppUser;
import com.example.harjoitustyo.security.SecurityUtils;
import com.example.harjoitustyo.service.FileStorageService;
import com.example.harjoitustyo.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile")
@PermitAll
public class UserProfileView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;
    private final FileStorageService storageService;
    private final Image image = new Image();
    private AppUser user;

    public UserProfileView(UserService userService, FileStorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
        addClassName("profile-view");
        setMaxWidth("760px");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!ViewGuards.requireLogin(event)) {
            return;
        }
        user = SecurityUtils.currentUsername()
                .flatMap(userService::findByUsername)
                .orElse(null);
        build();
    }

    private void build() {
        removeAll();
        if (user == null) {
            add(new H2("Profile was not found"));
            return;
        }

        TextField fullName = new TextField("Full name");
        fullName.setValue(user.getFullName());
        EmailField email = new EmailField("Email");
        email.setValue(user.getEmail());
        Button save = new Button("Save profile", event -> {
            user.setFullName(fullName.getValue());
            user.setEmail(email.getValue());
            userService.save(user);
            Notification.show("Profile saved");
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClassName("smooth-button");

        image.setAlt("Profile image");
        image.setWidth("140px");
        image.setHeight("140px");
        image.addClassName("profile-image");
        refreshImage();

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", ".jpg", ".jpeg", ".png");
        upload.addSucceededListener(event -> {
            try {
                String relative = storageService.saveProfileImage(event.getFileName(), buffer.getInputStream());
                userService.updatePhoto(user, relative);
                user.setPhotoFileName(relative);
                refreshImage();
                Notification.show("Profile image uploaded");
            } catch (IOException exception) {
                Notification.show("Upload failed: " + exception.getMessage());
            }
        });

        add(new H2("My profile"), new HorizontalLayout(image, new VerticalLayout(fullName, email, save)), upload);
    }

    private void refreshImage() {
        if (user.getPhotoFileName() == null) {
            image.setSrc("");
            return;
        }
        StreamResource resource = new StreamResource("profile-image", () -> {
            try {
                return Files.newInputStream(storageService.absolutePath(user.getPhotoFileName()));
            } catch (IOException exception) {
                return InputStream.nullInputStream();
            }
        });
        image.setSrc(resource);
    }
}
