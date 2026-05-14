package com.example.harjoitustyo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadRoot);
        Files.createDirectories(profileDir());
        Files.createDirectories(fileDir());
    }

    public String saveProfileImage(String originalFileName, InputStream inputStream) throws IOException {
        String fileName = "profile-" + UUID.randomUUID() + extension(originalFileName);
        Files.copy(inputStream, profileDir().resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return "profiles/" + fileName;
    }

    public String saveDocument(String originalFileName, InputStream inputStream) throws IOException {
        String safeName = originalFileName == null ? "file" : originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String fileName = UUID.randomUUID() + "-" + safeName;
        Files.copy(inputStream, fileDir().resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return "files/" + fileName;
    }

    public List<String> listDocuments() throws IOException {
        try (Stream<Path> files = Files.list(fileDir())) {
            return files.filter(Files::isRegularFile)
                    .map(path -> "files/" + path.getFileName().toString())
                    .sorted()
                    .toList();
        }
    }

    public Resource load(String relativePath) {
        return new FileSystemResource(uploadRoot.resolve(relativePath).normalize());
    }

    public Path absolutePath(String relativePath) {
        return uploadRoot.resolve(relativePath).normalize();
    }

    private Path profileDir() {
        return uploadRoot.resolve("profiles");
    }

    private Path fileDir() {
        return uploadRoot.resolve("files");
    }

    private String extension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            return ".bin";
        }
        return originalFileName.substring(originalFileName.lastIndexOf('.')).replaceAll("[^a-zA-Z0-9.]", "");
    }
}
