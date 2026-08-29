package com.shashi.distributedfilesystem.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;

@DynamoDbBean
public class FileMetadata {

    private String fileId;
    private String originalFileName;
    private String storedFileName;
    private long fileSize;
    private String contentType;
    private LocalDateTime uploadedAt;


    @DynamoDbPartitionKey
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }


    // ADDED: Default constructor required by DynamoDB
    public FileMetadata() {
    }


    public FileMetadata(String fileId,
                        String originalFileName,
                        String storedFileName,
                        long fileSize,
                        String contentType,
                        LocalDateTime uploadedAt) {

        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileId = fileId;
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