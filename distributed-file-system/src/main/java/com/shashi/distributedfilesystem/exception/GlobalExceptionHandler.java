package com.shashi.distributedfilesystem.exception;

import com.shashi.distributedfilesystem.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {

        ApiResponse response = new ApiResponse(
                "FAILED",
                ex.getMessage(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ApiResponse> handleFileNotFound(
            FileNotFoundException ex) {

        ApiResponse response = new ApiResponse(
                "FAILED",
                ex.getMessage(),
                null
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }
}