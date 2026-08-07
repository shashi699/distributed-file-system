package com.shashi.distributedfilesystem.controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.shashi.distributedfilesystem.model.ApiResponse;
import com.shashi.distributedfilesystem.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.List;

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

    @PostMapping("/upload")
    public ApiResponse uploadFile(@RequestParam("file") MultipartFile file)
            throws IOException {

        return fileStorageService.saveFile(file);
    }

}
