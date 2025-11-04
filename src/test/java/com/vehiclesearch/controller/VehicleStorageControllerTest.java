package com.vehiclesearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehiclesearch.model.StorageOption;
import com.vehiclesearch.model.StorageSpace;
import com.vehiclesearch.model.Vehicle;
import com.vehiclesearch.service.BinPackingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for VehicleStorageController
 */
@WebMvcTest(VehicleStorageController.class)
class VehicleStorageControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private BinPackingService binPackingService;
    
    @Test
    void testMatchVehicles_Success() throws Exception {
        // Setup
        List<Vehicle> vehicles = Arrays.asList(
            new Vehicle("v1", "sedan", 15.0, 6.0, 5.0)
        );
        
        StorageSpace space = new StorageSpace("space-1", "garage", 20.0, 10.0, 8.0, 
                                             150.0, "Downtown", Collections.emptyList());
        List<StorageOption> options = Arrays.asList(
            new StorageOption("v1", space, 85.0)
        );
        
        when(binPackingService.validateVehicle(any(Vehicle.class))).thenReturn(true);
        when(binPackingService.matchVehiclesToSpaces(any())).thenReturn(options);
        
        // Execute & Assert
        mockMvc.perform(post("/api/storage/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicles)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].vehicleId").value("v1"))
                .andExpect(jsonPath("$[0].matchedSpace").exists())
                .andExpect(jsonPath("$[0].fitScore").value(85.0));
    }
    
    @Test
    void testMatchVehicles_EmptyList() throws Exception {
        // Execute & Assert
        mockMvc.perform(post("/api/storage/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Vehicle list cannot be empty"));
    }
    
    @Test
    void testMatchVehicles_InvalidVehicle() throws Exception {
        // Setup - vehicle with negative dimension
        List<Vehicle> vehicles = Arrays.asList(
            new Vehicle("v1", "sedan", -15.0, 6.0, 5.0)
        );
        
        when(binPackingService.validateVehicle(any(Vehicle.class))).thenReturn(false);
        
        // Execute & Assert
        mockMvc.perform(post("/api/storage/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicles)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }
    
    @Test
    void testMatchVehicles_MultipleVehicles() throws Exception {
        // Setup
        List<Vehicle> vehicles = Arrays.asList(
            new Vehicle("v1", "sedan", 15.0, 6.0, 5.0),
            new Vehicle("v2", "suv", 17.0, 7.0, 6.0)
        );
        
        StorageSpace space1 = new StorageSpace("space-1", "garage", 20.0, 10.0, 8.0, 
                                              150.0, "Downtown", Collections.emptyList());
        StorageSpace space2 = new StorageSpace("space-2", "garage", 18.0, 9.0, 7.0, 
                                              100.0, "Midtown", Collections.emptyList());
        
        List<StorageOption> options = Arrays.asList(
            new StorageOption("v1", space1, 85.0),
            new StorageOption("v2", space2, 90.0)
        );
        
        when(binPackingService.validateVehicle(any(Vehicle.class))).thenReturn(true);
        when(binPackingService.matchVehiclesToSpaces(any())).thenReturn(options);
        
        // Execute & Assert
        mockMvc.perform(post("/api/storage/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicles)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].vehicleId").value("v1"))
                .andExpect(jsonPath("$[1].vehicleId").value("v2"));
    }
    
    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/storage/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Vehicle Storage Matcher"));
    }
}
