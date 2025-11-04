package com.vehiclesearch.service;

import com.vehiclesearch.model.StorageOption;
import com.vehiclesearch.model.StorageSpace;
import com.vehiclesearch.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service implementing bin packing algorithm to match vehicles to storage spaces.
 * Uses First-Fit Decreasing (FFD) bin packing heuristic.
 */
@Service
public class BinPackingService {
    
    private static final Logger logger = LoggerFactory.getLogger(BinPackingService.class);
    private final StorageSpaceService storageSpaceService;
    
    public BinPackingService(StorageSpaceService storageSpaceService) {
        this.storageSpaceService = storageSpaceService;
    }
    
    /**
     * Matches a list of vehicles to available storage spaces using bin packing algorithm.
     * Strategy: First-Fit Decreasing - sort vehicles by volume (largest first),
     * then assign each to the best-fitting available space.
     * 
     * @param vehicles List of vehicles to match
     * @return List of storage options with matched spaces
     */
    public List<StorageOption> matchVehiclesToSpaces(List<Vehicle> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<StorageOption> results = new ArrayList<>();
        List<StorageSpace> availableSpaces = new ArrayList<>(storageSpaceService.getAvailableStorageSpaces());
        
        // Sort vehicles by volume (descending) - larger vehicles matched first
        List<Vehicle> sortedVehicles = new ArrayList<>(vehicles);
        sortedVehicles.sort((v1, v2) -> Double.compare(v2.getVolume(), v1.getVolume()));
        
        logger.info("Matching {} vehicles to {} available storage spaces", vehicles.size(), availableSpaces.size());
        
        for (Vehicle vehicle : sortedVehicles) {
            StorageOption option = findBestFit(vehicle, availableSpaces);
            results.add(option);
            
            // Remove the matched space from available spaces to prevent double booking
            if (option.getMatchedSpace() != null) {
                availableSpaces.remove(option.getMatchedSpace());
                logger.info("Matched vehicle {} to space {}", vehicle.getId(), option.getMatchedSpace().getId());
            } else {
                logger.warn("No suitable space found for vehicle {}", vehicle.getId());
            }
        }
        
        return results;
    }
    
    /**
     * Finds the best fitting storage space for a vehicle.
     * Uses a scoring system that considers:
     * 1. Whether the space can physically fit the vehicle
     * 2. How efficiently the space is utilized (prefer tighter fits)
     * 3. Cost efficiency (price per unit volume)
     * 
     * @param vehicle The vehicle to match
     * @param availableSpaces List of available storage spaces
     * @return StorageOption with the best match or no match
     */
    private StorageOption findBestFit(Vehicle vehicle, List<StorageSpace> availableSpaces) {
        StorageSpace bestMatch = null;
        double bestScore = Double.MAX_VALUE;
        
        for (StorageSpace space : availableSpaces) {
            if (!space.canFit(vehicle)) {
                continue;
            }
            
            // Calculate fit score (lower is better)
            double score = calculateFitScore(vehicle, space);
            
            if (score < bestScore) {
                bestScore = score;
                bestMatch = space;
            }
        }
        
        if (bestMatch == null) {
            return new StorageOption(vehicle.getId(), "No suitable storage space found");
        }
        
        // Normalize score to 0-100 range (100 is best fit)
        double normalizedScore = Math.max(0, Math.min(100, 100 - bestScore));
        return new StorageOption(vehicle.getId(), bestMatch, normalizedScore);
    }
    
    /**
     * Calculates a fit score for a vehicle-space pairing.
     * Lower score is better. Considers:
     * - Wasted space (difference in volumes)
     * - Cost per cubic unit
     * - Dimensional efficiency
     * 
     * @param vehicle The vehicle
     * @param space The storage space
     * @return Fit score (lower is better)
     */
    private double calculateFitScore(Vehicle vehicle, StorageSpace space) {
        double vehicleVolume = vehicle.getVolume();
        double spaceVolume = space.getVolume();
        
        // Waste ratio: how much space is wasted (0 = perfect fit, 1 = very wasteful)
        double wasteRatio = (spaceVolume - vehicleVolume) / spaceVolume;
        
        // Cost efficiency: price per cubic unit
        double costPerUnit = space.getPricePerMonth() / spaceVolume;
        
        // Dimensional fit: how well dimensions match
        double lengthRatio = space.getLength() / vehicle.getLength();
        double widthRatio = space.getWidth() / vehicle.getWidth();
        double heightRatio = space.getHeight() / vehicle.getHeight();
        double avgDimensionRatio = (lengthRatio + widthRatio + heightRatio) / 3.0;
        double dimensionEfficiency = Math.abs(avgDimensionRatio - 1.0);
        
        // Combined score (weighted)
        double score = (wasteRatio * 40.0) + (costPerUnit * 30.0) + (dimensionEfficiency * 30.0);
        
        return score;
    }
    
    /**
     * Validates a single vehicle's dimensions
     * 
     * @param vehicle Vehicle to validate
     * @return true if valid, false otherwise
     */
    public boolean validateVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            return false;
        }
        if (vehicle.getId() == null || vehicle.getId().trim().isEmpty()) {
            return false;
        }
        if (vehicle.getLength() == null || vehicle.getLength() <= 0) {
            return false;
        }
        if (vehicle.getWidth() == null || vehicle.getWidth() <= 0) {
            return false;
        }
        if (vehicle.getHeight() == null || vehicle.getHeight() <= 0) {
            return false;
        }
        return true;
    }
}
