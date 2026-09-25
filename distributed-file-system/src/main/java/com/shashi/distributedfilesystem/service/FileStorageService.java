package com.shashi.distributedfilesystem.service;

import org.springframework.core.io.Resource;
import java.util.List;

import com.shashi.distributedfilesystem.model.ApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.shashi.distributedfilesystem.model.FileMetadata;
import java.time.LocalDateTime;
import java.io.IOException;
import java.util.UUID;

import com.shashi.distributedfilesystem.exception.FileNotFoundException;

@Service
public class FileStorageService {

    private final MetadataService metadataService;
    private final S3StorageService s3StorageService;

    public FileStorageService(
            MetadataService metadataService,
            S3StorageService s3StorageService) {

        this.metadataService = metadataService;
        this.s3StorageService = s3StorageService;
    }

    public ApiResponse saveFile(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload an empty file.");
        }

        long maxFileSize = 5 * 1024 * 1024; // 5 MB

        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds 5 MB.");
        }

        String fileId = UUID.randomUUID().toString();

        String uniqueFileName =
                fileId + "_" + file.getOriginalFilename();

        // Upload file to S3
        s3StorageService.uploadFile(
                file,
                uniqueFileName
        );

        // Save metadata to DynamoDB
        FileMetadata metadata = new FileMetadata(
                fileId,
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

    public Resource downloadFile(String storedFileName) {

        try {

            java.io.InputStream inputStream =
                    s3StorageService.downloadFile(storedFileName);

            return new org.springframework.core.io.InputStreamResource(
                    inputStream
            );

        } catch (Exception e) {

            throw new FileNotFoundException(
                    "File not found in S3: " + storedFileName
            );
        }
    }

    public List<String> listFiles() {
        return metadataService.getAllFileNames();
    }

    public ApiResponse deleteFile(String fileName) {

        // Find metadata using the stored filename
        FileMetadata metadata = metadataService
                .findMetadataByStoredFileName(fileName)
                .orElse(null);

        if (metadata == null) {
            throw new FileNotFoundException(
                    "File not found: " + fileName
            );
        }

        // Delete file from S3
        s3StorageService.deleteFile(fileName);

        // Delete metadata from DynamoDB
        metadataService.deleteMetadata(
                metadata.getFileId()
        );

        return new ApiResponse(
                "SUCCESS",
                "File deleted successfully",
                fileName
        );
    }

    public void renameFile(
            String oldStoredFileName,
            String newStoredFileName) {

        s3StorageService.renameFile(
                oldStoredFileName,
                newStoredFileName
        );
    }
}