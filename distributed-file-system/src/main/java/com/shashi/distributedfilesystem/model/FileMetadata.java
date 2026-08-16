package com.shashi.distributedfilesystem.model;

import java.time.LocalDateTime;

public class FileMetadata {

    private String originalFileName;
    private String storedFileName;
    private long fileSize;
    private String contentType;
    private LocalDateTime uploadedAt;

    public FileMetadata() {
    }

    public FileMetadata(String originalFileNamefileName,
                        String storedFileName,
                        long fileSize,
                        String contentType,
                        LocalDateTime uploadedAt) {
        this.originalFileName = originalFileNamefileName;
        this.storedFileName = storedFileName;

        this.fileSize = fileSize;
        this.contentType = contentType;
        this.uploadedAt = uploadedAt;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}