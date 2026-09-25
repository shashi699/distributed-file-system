package com.shashi.distributedfilesystem.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
public class S3StorageService {

    private static final String BUCKET_NAME =
            "shashi-distributed-file-system-2026";

    private final S3Client s3Client;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(MultipartFile file, String storedFileName)
            throws IOException {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(storedFileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(
                        file.getInputStream(),
                        file.getSize()
                )
        );
    }
    public InputStream downloadFile(String storedFileName) {

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(storedFileName)
                .build();

        ResponseBytes<GetObjectResponse> response =
                s3Client.getObjectAsBytes(request);

        return new ByteArrayInputStream(response.asByteArray());
    }
    public void deleteFile(String storedFileName) {

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(storedFileName)
                .build();

        s3Client.deleteObject(request);
    }

    public void renameFile(String oldStoredFileName,
                           String newStoredFileName) {

        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(BUCKET_NAME)
                .sourceKey(oldStoredFileName)
                .destinationBucket(BUCKET_NAME)
                .destinationKey(newStoredFileName)
                .build();

        s3Client.copyObject(copyRequest);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(oldStoredFileName)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}