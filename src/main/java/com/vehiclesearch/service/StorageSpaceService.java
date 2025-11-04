package com.vehiclesearch.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehiclesearch.model.StorageSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to load and manage storage space listings
 */
@Service
public class StorageSpaceService {
    
    private static final Logger logger = LoggerFactory.getLogger(StorageSpaceService.class);
    private final ObjectMapper objectMapper;
    private List<StorageSpace> storageSpaces;
    
    public StorageSpaceService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.storageSpaces = new ArrayList<>();
    }
    
    @PostConstruct
    public void loadStorageSpaces() {
        try {
            ClassPathResource resource = new ClassPathResource("listings.json");
            InputStream inputStream = resource.getInputStream();
            storageSpaces = objectMapper.readValue(inputStream, new TypeReference<List<StorageSpace>>() {});
            logger.info("Loaded {} storage spaces from listings.json", storageSpaces.size());
        } catch (IOException e) {
            logger.error("Failed to load storage spaces from listings.json", e);
            storageSpaces = new ArrayList<>();
        }
    }
    
    public List<StorageSpace> getAllStorageSpaces() {
        return new ArrayList<>(storageSpaces);
    }
    
    public List<StorageSpace> getAvailableStorageSpaces() {
        // In a real implementation, this would filter out already occupied spaces
        return getAllStorageSpaces();
    }
}
