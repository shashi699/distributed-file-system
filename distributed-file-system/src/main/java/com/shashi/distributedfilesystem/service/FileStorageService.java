

package com.shashi.distributedfilesystem.service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.net.MalformedURLException;
import com.shashi.distributedfilesystem.model.ApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.shashi.distributedfilesystem.model.FileMetadata;
import java.time.LocalDateTime;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.shashi.distributedfilesystem.exception.FileNotFoundException;

@Service
public class FileStorageService {

    private final MetadataService metadataService;

    private static final Path UPLOAD_DIR =
            Paths.get(System.getProperty("user.dir"), "uploads");

    public FileStorageService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    public ApiResponse saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload an empty file.");
        }

        long maxFileSize = 5 * 1024 * 1024; // 5 MB

        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds 5 MB.");
        }

        Files.createDirectories(UPLOAD_DIR);

        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path destination = UPLOAD_DIR.resolve(uniqueFileName);

        file.transferTo(destination);

        FileMetadata metadata = new FileMetadata(
                file.getOriginalFilename(),
                uniqueFileName,
                file.getSize(),
                file.getContentType(),
                LocalDateTime.now()
        );

        metadataService.saveMetadata(metadata);

        ApiResponse response = new ApiResponse(
                "SUCCESS",
                "File uploaded successfully",
                file.getOriginalFilename()
        );

        response.setMetadata(metadata);

        return response;
    }
    public Resource downloadFile(String fileName) throws MalformedURLException {

        Path filePath = UPLOAD_DIR.resolve(fileName).normalize();

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        return resource;
    }
    public List<String> listFiles() throws IOException {

        try (Stream<Path> paths = Files.list(UPLOAD_DIR)) {

            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        }
    }
    public ApiResponse deleteFile(String fileName) throws IOException {

        Path filePath = UPLOAD_DIR.resolve(fileName);

        if (Files.exists(filePath)) {

            Files.delete(filePath);

            return new ApiResponse(
                    "SUCCESS",
                    "File deleted successfully",
                    fileName
            );
        }

        throw new FileNotFoundException("File not found: " + fileName);
        
    }
    public String renameFile(String oldStoredFileName, String newStoredFileName)
            throws IOException {

        Path oldPath = UPLOAD_DIR.resolve(oldStoredFileName);
        Path newPath = UPLOAD_DIR.resolve(newStoredFileName);

        if (!Files.exists(oldPath)) {
            throw new RuntimeException("File not found: " + oldStoredFileName);
        }

        Files.move(oldPath, newPath);

        return newStoredFileName;
    }
}