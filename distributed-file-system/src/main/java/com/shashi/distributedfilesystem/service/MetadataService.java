package com.shashi.distributedfilesystem.service;

import com.shashi.distributedfilesystem.model.FileMetadata;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;


import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class MetadataService {

    private final DynamoDbTable<FileMetadata> metadataTable;

    public MetadataService(DynamoDbEnhancedClient enhancedClient) {

        this.metadataTable = enhancedClient.table(
                "Filemetadata",
                TableSchema.fromBean(FileMetadata.class)
        );
    }

    // ---------------------------------------------------------
    // SAVE METADATA
    // ---------------------------------------------------------

    public void saveMetadata(FileMetadata metadata) {

        metadataTable.putItem(metadata);
    }

    // ---------------------------------------------------------
    // GET METADATA BY FILE ID
    // ---------------------------------------------------------

    public FileMetadata getMetadata(String fileId) {

        return metadataTable.getItem(
                r -> r.key(
                        k -> k.partitionValue(fileId)
                )
        );
    }

    // ---------------------------------------------------------
    // SEARCH METADATA BY ORIGINAL FILE NAME
    // ---------------------------------------------------------

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

    // ---------------------------------------------------------
    // FIND METADATA BY ORIGINAL FILE NAME
    // ---------------------------------------------------------

    public Optional<FileMetadata> findMetadataByOriginalName(
            String oldFileName) {

        return metadataTable.scan()
                .items()
                .stream()
                .filter(metadata ->
                        metadata.getOriginalFileName()
                                .equals(oldFileName)
                )
                .findFirst();
    }

    // ---------------------------------------------------------
    // RENAME METADATA
    // ---------------------------------------------------------

    public FileMetadata renameMetadata(
            String oldFileName,
            String newFileName) {

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

    // ---------------------------------------------------------
    // UPDATE STORED FILE NAME
    // ---------------------------------------------------------

    public void updateStoredFileName(
            String fileId,
            FileMetadata metadata) {

        metadataTable.putItem(metadata);
    }

    // ---------------------------------------------------------
    // FIND METADATA BY STORED FILE NAME
    // ---------------------------------------------------------

    public Optional<FileMetadata> findMetadataByStoredFileName(
            String storedFileName) {

        return metadataTable.scan()
                .items()
                .stream()
                .filter(metadata ->
                        metadata.getStoredFileName()
                                .equals(storedFileName)
                )
                .findFirst();
    }

    // ---------------------------------------------------------
    // DELETE METADATA
    // ---------------------------------------------------------

    public void deleteMetadata(String fileId) {

        metadataTable.deleteItem(
                r -> r.key(
                        k -> k.partitionValue(fileId)
                )
        );
    }

    public List<String> getAllFileNames() {
        return metadataTable.scan()
                .items()
                .stream()
                .map(FileMetadata::getOriginalFileName)
                .collect(Collectors.toList());
    }
}