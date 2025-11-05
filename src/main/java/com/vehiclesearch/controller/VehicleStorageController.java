package com.vehiclesearch.controller;

import com.vehiclesearch.model.StorageOption;
import com.vehiclesearch.model.Vehicle;
import com.vehiclesearch.service.BinPackingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for vehicle storage matching
 */
@RestController
@RequestMapping("/api/storage")
public class VehicleStorageController {
    
    private static final Logger logger = LoggerFactory.getLogger(VehicleStorageController.class);
    private final BinPackingService binPackingService;
    
    public VehicleStorageController(BinPackingService binPackingService) {
        this.binPackingService = binPackingService;
    }
    
    /**
     * POST endpoint to match vehicles to storage spaces
     * 
     * @param vehicles JSON array of vehicle dimensions
     * @param bindingResult Validation results
     * @return JSON array of storage options
     */
    @PostMapping("/match")
    public ResponseEntity<?> matchVehicles(@Valid @RequestBody List<Vehicle> vehicles, 
                                          BindingResult bindingResult) {
        logger.info("Received request to match {} vehicles", vehicles != null ? vehicles.size() : 0);
        
        // Input validation
        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Validation failed");
            errorResponse.put("errors", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList()));
            logger.warn("Validation errors: {}", errorResponse.get("errors"));
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        if (vehicles == null || vehicles.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Vehicle list cannot be empty");
            logger.warn("Empty vehicle list received");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Additional validation
        for (Vehicle vehicle : vehicles) {
            if (!binPackingService.validateVehicle(vehicle)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Invalid vehicle data for vehicle: " + vehicle.getId());
                logger.warn("Invalid vehicle data: {}", vehicle);
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }
        
        try {
            // Match vehicles to storage spaces
            List<StorageOption> options = binPackingService.matchVehiclesToSpaces(vehicles);
            logger.info("Successfully matched {} vehicles to storage spaces", vehicles.size());
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            logger.error("Error processing vehicle matching request", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Vehicle Storage Matcher");
        return ResponseEntity.ok(response);
    }
}
