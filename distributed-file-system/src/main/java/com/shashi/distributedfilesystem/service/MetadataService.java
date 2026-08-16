package com.shashi.distributedfilesystem.service;

import com.shashi.distributedfilesystem.model.FileMetadata;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;

@Service
public class MetadataService {

    private final Map<String, FileMetadata> metadataStore = new HashMap<>();

    public void saveMetadata(FileMetadata metadata) {

        metadataStore.put(
                metadata.getStoredFileName(),
                metadata
        );

    }


    public FileMetadata getMetadata(String storedFileName) {
        return metadataStore.get(storedFileName);
    }

    public List<FileMetadata> searchMetadata(String name) {

        return metadataStore.values()
                .stream()
                .filter(metadata ->
                        metadata.getOriginalFileName()
                                .toLowerCase()
                                .contains(name.toLowerCase())
                )
                .collect(Collectors.toList());
    }


    public Optional<FileMetadata> findMetadataByOriginalName(String oldFileName) {

        return metadataStore.values()
                .stream()
                .filter(metadata ->
                        metadata.getOriginalFileName().equals(oldFileName)
                )
                .findFirst();
    }
    public FileMetadata renameMetadata(String oldFileName, String newFileName) {

        Optional<FileMetadata> metadataOptional =
                findMetadataByOriginalName(oldFileName);

        if (metadataOptional.isEmpty()) {
            return null;
        }

        FileMetadata metadata = metadataOptional.get();

        metadata.setOriginalFileName(newFileName);

        return metadata;
    }
    public void updateStoredFileName(
            String oldStoredFileName,
            String newStoredFileName,
            FileMetadata metadata) {

        metadataStore.remove(oldStoredFileName);

        metadataStore.put(newStoredFileName, metadata);
    }
}
