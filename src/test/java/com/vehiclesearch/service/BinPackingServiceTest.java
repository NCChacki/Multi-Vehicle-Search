package com.vehiclesearch.service;

import com.vehiclesearch.model.StorageOption;
import com.vehiclesearch.model.StorageSpace;
import com.vehiclesearch.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for BinPackingService
 */
class BinPackingServiceTest {
    
    @Mock
    private StorageSpaceService storageSpaceService;
    
    private BinPackingService binPackingService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        binPackingService = new BinPackingService(storageSpaceService);
    }
    
    @Test
    void testMatchVehiclesToSpaces_Success() {
        // Setup mock data
        List<StorageSpace> spaces = Arrays.asList(
            new StorageSpace("space-1", "garage", 20.0, 10.0, 8.0, 150.0, "Downtown", Collections.emptyList()),
            new StorageSpace("space-2", "garage", 18.0, 9.0, 7.0, 100.0, "Midtown", Collections.emptyList())
        );
        
        List<Vehicle> vehicles = Arrays.asList(
            new Vehicle("vehicle-1", "sedan", 15.0, 6.0, 5.0),
            new Vehicle("vehicle-2", "suv", 17.0, 7.0, 6.0)
        );
        
        when(storageSpaceService.getAvailableStorageSpaces()).thenReturn(spaces);
        
        // Execute
        List<StorageOption> results = binPackingService.matchVehiclesToSpaces(vehicles);
        
        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertNotNull(results.get(0).getMatchedSpace());
        assertNotNull(results.get(1).getMatchedSpace());
    }
    
    @Test
    void testMatchVehiclesToSpaces_NoSuitableSpace() {
        // Setup mock data - vehicle too large for any space
        List<StorageSpace> spaces = Arrays.asList(
            new StorageSpace("space-1", "garage", 10.0, 5.0, 4.0, 50.0, "Downtown", Collections.emptyList())
        );
        
        List<Vehicle> vehicles = Arrays.asList(
            new Vehicle("vehicle-1", "truck", 20.0, 10.0, 8.0)
        );
        
        when(storageSpaceService.getAvailableStorageSpaces()).thenReturn(spaces);
        
        // Execute
        List<StorageOption> results = binPackingService.matchVehiclesToSpaces(vehicles);
        
        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertNull(results.get(0).getMatchedSpace());
        assertTrue(results.get(0).getMessage().contains("No suitable"));
    }
    
    @Test
    void testMatchVehiclesToSpaces_EmptyVehicleList() {
        // Execute
        List<StorageOption> results = binPackingService.matchVehiclesToSpaces(Collections.emptyList());
        
        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
    
    @Test
    void testMatchVehiclesToSpaces_NullVehicleList() {
        // Execute
        List<StorageOption> results = binPackingService.matchVehiclesToSpaces(null);
        
        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
    
    @Test
    void testMatchVehiclesToSpaces_SortsByVolume() {
        // Setup mock data
        List<StorageSpace> spaces = Arrays.asList(
            new StorageSpace("space-1", "garage", 20.0, 10.0, 8.0, 150.0, "Downtown", Collections.emptyList()),
            new StorageSpace("space-2", "garage", 15.0, 8.0, 6.0, 100.0, "Midtown", Collections.emptyList())
        );
        
        // Small vehicle and large vehicle
        List<Vehicle> vehicles = Arrays.asList(
            new Vehicle("vehicle-small", "sedan", 10.0, 5.0, 4.0),
            new Vehicle("vehicle-large", "truck", 18.0, 9.0, 7.0)
        );
        
        when(storageSpaceService.getAvailableStorageSpaces()).thenReturn(spaces);
        
        // Execute
        List<StorageOption> results = binPackingService.matchVehiclesToSpaces(vehicles);
        
        // Assert - larger vehicle should get the larger space
        assertNotNull(results);
        assertEquals(2, results.size());
        // Both should have matches
        assertNotNull(results.get(0).getMatchedSpace());
        assertNotNull(results.get(1).getMatchedSpace());
    }
    
    @Test
    void testValidateVehicle_Valid() {
        Vehicle vehicle = new Vehicle("v1", "sedan", 15.0, 6.0, 5.0);
        assertTrue(binPackingService.validateVehicle(vehicle));
    }
    
    @Test
    void testValidateVehicle_NullVehicle() {
        assertFalse(binPackingService.validateVehicle(null));
    }
    
    @Test
    void testValidateVehicle_NullId() {
        Vehicle vehicle = new Vehicle(null, "sedan", 15.0, 6.0, 5.0);
        assertFalse(binPackingService.validateVehicle(vehicle));
    }
    
    @Test
    void testValidateVehicle_EmptyId() {
        Vehicle vehicle = new Vehicle("", "sedan", 15.0, 6.0, 5.0);
        assertFalse(binPackingService.validateVehicle(vehicle));
    }
    
    @Test
    void testValidateVehicle_NullDimension() {
        Vehicle vehicle = new Vehicle("v1", "sedan", null, 6.0, 5.0);
        assertFalse(binPackingService.validateVehicle(vehicle));
    }
    
    @Test
    void testValidateVehicle_NegativeDimension() {
        Vehicle vehicle = new Vehicle("v1", "sedan", -15.0, 6.0, 5.0);
        assertFalse(binPackingService.validateVehicle(vehicle));
    }
    
    @Test
    void testValidateVehicle_ZeroDimension() {
        Vehicle vehicle = new Vehicle("v1", "sedan", 0.0, 6.0, 5.0);
        assertFalse(binPackingService.validateVehicle(vehicle));
    }
}
