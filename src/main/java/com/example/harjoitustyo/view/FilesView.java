package com.example.harjoitustyo.view;

import com.example.harjoitustyo.service.CsvService;
import com.example.harjoitustyo.service.FileStorageService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Route(value = "files", layout = MainLayout.class)
@PageTitle("Files & CSV")
@PermitAll
public class FilesView extends VerticalLayout implements BeforeEnterObserver {

    private final FileStorageService storageService;
    private final CsvService csvService;
    private final Div documentList = new Div();

    public FilesView(FileStorageService storageService, CsvService csvService) {
        this.storageService = storageService;
        this.csvService = csvService;
        addClassName("files-view");
        setSizeFull();

        MemoryBuffer fileBuffer = new MemoryBuffer();
        Upload fileUpload = new Upload(fileBuffer);
        fileUpload.setDropLabel(new Div("Upload and store a file"));
        fileUpload.addSucceededListener(event -> {
            try {
                storageService.saveDocument(event.getFileName(), fileBuffer.getInputStream());
                Notification.show("File uploaded");
                refreshDocuments();
            } catch (IOException exception) {
                Notification.show("Upload failed: " + exception.getMessage());
            }
        });

        Anchor export = new Anchor(projectExportResource(), "Export projects CSV");
        export.getElement().setAttribute("download", true);
        export.addClassName("download-link");

        MemoryBuffer csvBuffer = new MemoryBuffer();
        Upload csvUpload = new Upload(csvBuffer);
        csvUpload.setAcceptedFileTypes(".csv", "text/csv");
        csvUpload.setDropLabel(new Div("Import projects CSV"));
        csvUpload.addSucceededListener(event -> {
            try {
                int count = csvService.importProjects(csvBuffer.getInputStream());
                Notification.show("Imported " + count + " projects");
            } catch (Exception exception) {
                Notification.show("CSV import failed: " + exception.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });

        Button refresh = new Button("Refresh files", event -> refreshDocuments());
        refresh.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        refresh.addClassName("smooth-button");

        QuillEditor editor = new QuillEditor();
        add(new H2("File upload and CSV"), new HorizontalLayout(fileUpload, csvUpload), export, refresh, documentList,
                new H2("External JavaScript component: Quill.js"), editor);
        refreshDocuments();
    }

    private StreamResource projectExportResource() {
        return new StreamResource("projects.csv", () -> {
            try {
                return new ByteArrayInputStream(csvService.exportProjects().getBytes(StandardCharsets.UTF_8));
            } catch (IOException exception) {
                return new ByteArrayInputStream(("error," + exception.getMessage()).getBytes(StandardCharsets.UTF_8));
            }
        });
    }

    private void refreshDocuments() {
        documentList.removeAll();
        documentList.add(new H2("Stored files"));
        try {
            for (String relative : storageService.listDocuments()) {
                Anchor link = new Anchor(downloadResource(relative), relative);
                link.getElement().setAttribute("download", true);
                documentList.add(new Div(link));
            }
        } catch (IOException exception) {
            documentList.add(new Div("Cannot list files: " + exception.getMessage()));
        }
    }

    private StreamResource downloadResource(String relative) {
        return new StreamResource(relative.substring(relative.lastIndexOf('/') + 1), () -> {
            try {
                return Files.newInputStream(storageService.absolutePath(relative));
            } catch (IOException exception) {
                return InputStream.nullInputStream();
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        ViewGuards.requireLogin(event);
    }
}
