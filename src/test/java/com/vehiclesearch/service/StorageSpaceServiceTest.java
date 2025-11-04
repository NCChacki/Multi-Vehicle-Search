package com.vehiclesearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehiclesearch.model.StorageSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StorageSpaceService
 */
class StorageSpaceServiceTest {
    
    private StorageSpaceService storageSpaceService;
    
    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        storageSpaceService = new StorageSpaceService(objectMapper);
        storageSpaceService.loadStorageSpaces();
    }
    
    @Test
    void testLoadStorageSpaces() {
        List<StorageSpace> spaces = storageSpaceService.getAllStorageSpaces();
        assertNotNull(spaces);
        assertFalse(spaces.isEmpty());
        assertTrue(spaces.size() > 0);
    }
    
    @Test
    void testGetAllStorageSpaces() {
        List<StorageSpace> spaces = storageSpaceService.getAllStorageSpaces();
        assertNotNull(spaces);
        
        // Verify structure of first space
        if (!spaces.isEmpty()) {
            StorageSpace firstSpace = spaces.get(0);
            assertNotNull(firstSpace.getId());
            assertNotNull(firstSpace.getType());
            assertNotNull(firstSpace.getLength());
            assertNotNull(firstSpace.getWidth());
            assertNotNull(firstSpace.getHeight());
            assertNotNull(firstSpace.getPricePerMonth());
        }
    }
    
    @Test
    void testGetAvailableStorageSpaces() {
        List<StorageSpace> spaces = storageSpaceService.getAvailableStorageSpaces();
        assertNotNull(spaces);
        assertEquals(storageSpaceService.getAllStorageSpaces().size(), spaces.size());
    }
    
    @Test
    void testStorageSpaceCanFit() {
        List<StorageSpace> spaces = storageSpaceService.getAllStorageSpaces();
        assertFalse(spaces.isEmpty());
        
        StorageSpace space = spaces.get(0);
        
        // Create a vehicle that fits
        com.vehiclesearch.model.Vehicle smallVehicle = 
            new com.vehiclesearch.model.Vehicle("v1", "sedan", 
                space.getLength() - 1, 
                space.getWidth() - 1, 
                space.getHeight() - 1);
        
        assertTrue(space.canFit(smallVehicle));
        
        // Create a vehicle that doesn't fit
        com.vehiclesearch.model.Vehicle largeVehicle = 
            new com.vehiclesearch.model.Vehicle("v2", "truck", 
                space.getLength() + 10, 
                space.getWidth() + 10, 
                space.getHeight() + 10);
        
        assertFalse(space.canFit(largeVehicle));
    }
}
