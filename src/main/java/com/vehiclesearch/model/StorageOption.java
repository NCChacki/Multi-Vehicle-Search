package com.vehiclesearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a matched storage option for a vehicle
 */
public class StorageOption {
    
    @JsonProperty("vehicleId")
    private String vehicleId;
    
    @JsonProperty("matchedSpace")
    private StorageSpace matchedSpace;
    
    @JsonProperty("fitScore")
    private Double fitScore;
    
    @JsonProperty("message")
    private String message;
    
    public StorageOption() {
    }
    
    public StorageOption(String vehicleId, StorageSpace matchedSpace, Double fitScore) {
        this.vehicleId = vehicleId;
        this.matchedSpace = matchedSpace;
        this.fitScore = fitScore;
        this.message = matchedSpace != null ? "Match found" : "No suitable storage space found";
    }
    
    public StorageOption(String vehicleId, String message) {
        this.vehicleId = vehicleId;
        this.message = message;
        this.fitScore = 0.0;
    }
    
    // Getters and Setters
    public String getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public StorageSpace getMatchedSpace() {
        return matchedSpace;
    }
    
    public void setMatchedSpace(StorageSpace matchedSpace) {
        this.matchedSpace = matchedSpace;
    }
    
    public Double getFitScore() {
        return fitScore;
    }
    
    public void setFitScore(Double fitScore) {
        this.fitScore = fitScore;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "StorageOption{" +
                "vehicleId='" + vehicleId + '\'' +
                ", matchedSpace=" + (matchedSpace != null ? matchedSpace.getId() : "null") +
                ", fitScore=" + fitScore +
                ", message='" + message + '\'' +
                '}';
    }
}
