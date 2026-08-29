package com.shashi.distributedfilesystem.controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.shashi.distributedfilesystem.model.ApiResponse;
import com.shashi.distributedfilesystem.service.FileStorageService;
import com.shashi.distributedfilesystem.service.MetadataService;
import com.shashi.distributedfilesystem.model.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.List;

import com.shashi.distributedfilesystem.model.RenameRequest;
import com.shashi.distributedfilesystem.exception.FileNotFoundException;

@RestController

@RequestMapping("/files")

public class FileController {


    @DeleteMapping("/{fileName}")
    public ApiResponse deleteFile(@PathVariable String fileName)
            throws IOException {

        return fileStorageService.deleteFile(fileName);
    }


    @GetMapping
    public List<String> listFiles() throws IOException {

        return fileStorageService.listFiles();

    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName)
            throws Exception {

        Resource resource = fileStorageService.downloadFile(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private MetadataService metadataService;

    @PostMapping("/upload")
    public ApiResponse uploadFile(@RequestParam("file") MultipartFile file)
            throws IOException {

        return fileStorageService.saveFile(file);
    }
    @GetMapping("/metadata/{fileId}")
    public ResponseEntity<FileMetadata> getMetadata(
            @PathVariable String fileId) {

        FileMetadata metadata =
                metadataService.getMetadata(fileId);

        if (metadata == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(metadata);
    }
    @GetMapping("/search")
    public List<FileMetadata> searchFiles(@RequestParam String name) {

        return metadataService.searchMetadata(name);
    }
    @PutMapping("/rename")
    public ApiResponse renameFile(@RequestBody RenameRequest request)
            throws IOException {

        String oldFileName = request.getOldFileName();
        String newFileName = request.getNewFileName();

        FileMetadata metadata =
                metadataService.findMetadataByOriginalName(oldFileName)
                        .orElse(null);

        if (metadata == null) {
            throw new FileNotFoundException(
                    "File not found: " + oldFileName
            );
        }

        String oldStoredFileName = metadata.getStoredFileName();

        String uuid = oldStoredFileName.substring(
                0,
                oldStoredFileName.indexOf("_")
        );

        String newStoredFileName = uuid + "_" + newFileName;

        fileStorageService.renameFile(
                oldStoredFileName,
                newStoredFileName
        );

        metadata.setOriginalFileName(newFileName);
        metadata.setStoredFileName(newStoredFileName);

        metadataService.updateStoredFileName(
                metadata.getFileId(),
                metadata
        );

        return new ApiResponse(
                "SUCCESS",
                "File renamed successfully",
                newFileName
        );
    }
}