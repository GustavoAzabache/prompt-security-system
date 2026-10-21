package com.me.microservices.document_service.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.me.microservices.document_service.storage.dto.StoredFile;

@Service
public class FileStorageService {

    private final Path storageLocation;

    public FileStorageService(StorageProperties properties) {
        this.storageLocation = Paths
                                .get(properties.location())
                                .toAbsolutePath()
                                .normalize();

        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not initialize file storage",
                    exception
            );
        }
    }

    public StoredFile store(MultipartFile file, UUID documentId) {

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException(
                    "File name is required"
            );
        }

        String extension = getExtension(originalFilename);

        String storedFilename = documentId + extension;

        Path destination = storageLocation.resolve(storedFilename);

        try {
            file.transferTo(destination);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not store file",
                    exception
            );
        }

        return new StoredFile(
            documentId,
            originalFilename,
            file.getSize(),
            destination.toString()
        );
    }

    private String getExtension(String filename) {

        int lastDot = filename.lastIndexOf('.');

        if (lastDot == -1) {
            return "";
        }

        return filename.substring(lastDot);
    }
}
