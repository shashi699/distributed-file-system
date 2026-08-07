package com.shashi.distributedfilesystem.model;

public class ApiResponse {

    private String status;
    private String message;
    private String fileName;

    public ApiResponse() {
    }

    public ApiResponse(String status, String message, String fileName) {
        this.status = status;
        this.message = message;
        this.fileName = fileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}