

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private static final Path UPLOAD_DIR =
            Paths.get(System.getProperty("user.dir"), "uploads");


    public ApiResponse saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload an empty file.");
        }

        long maxFileSize = 5 * 1024 * 1024; // 5 MB

        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds 5 MB.");
        }

        Files.createDirectories(UPLOAD_DIR);

        Path destination = UPLOAD_DIR.resolve(file.getOriginalFilename());

        file.transferTo(destination);

        return new ApiResponse(
                "SUCCESS",
                "File uploaded successfully",
                file.getOriginalFilename()
        );
    }
    public Resource downloadFile(String fileName) throws MalformedURLException {

        Path filePath = UPLOAD_DIR.resolve(fileName).normalize();

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found: " + fileName);
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

        return new ApiResponse(
                "FAILED",
                "File not found",
                fileName
        );
    }
}