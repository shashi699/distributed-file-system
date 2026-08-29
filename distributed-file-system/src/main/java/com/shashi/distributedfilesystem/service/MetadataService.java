package com.shashi.distributedfilesystem.service;

import com.shashi.distributedfilesystem.model.FileMetadata;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;

@Service
public class MetadataService {

    private final Map<String, FileMetadata> metadataStore = new HashMap<>();
    private final DynamoDbTable<FileMetadata> metadataTable;

    public void saveMetadata(FileMetadata metadata) {

        metadataTable.putItem(metadata);
    }

    public MetadataService(DynamoDbEnhancedClient enhancedClient) {

        this.metadataTable = enhancedClient.table(
                "Filemetadata",
                TableSchema.fromBean(FileMetadata.class)
        );
    }

    public FileMetadata getMetadata(String fileId) {

        return metadataTable.getItem(
                r -> r.key(
                        k -> k.partitionValue(fileId)
                )
        );
    }

    public List<FileMetadata> searchMetadata(String name) {

        return metadataTable.scan()
                .items()
                .stream()
                .filter(metadata ->
                        metadata.getOriginalFileName()
                                .toLowerCase()
                                .contains(name.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    public Optional<FileMetadata> findMetadataByOriginalName(String oldFileName) {

        return metadataTable.scan()
                .items()
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

        metadataTable.putItem(metadata);

        return metadata;
    }

    public void updateStoredFileName(
            String fileId,
            FileMetadata metadata) {

        metadataTable.putItem(metadata);
    }
}