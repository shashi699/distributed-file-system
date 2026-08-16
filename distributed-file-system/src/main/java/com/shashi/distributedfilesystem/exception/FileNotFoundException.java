package com.shashi.distributedfilesystem.exception;

public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String message) {
        super(message);
    }
}