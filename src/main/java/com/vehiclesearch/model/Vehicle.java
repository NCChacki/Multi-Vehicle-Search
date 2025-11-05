package com.vehiclesearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents a vehicle with its dimensions
 */
public class Vehicle {
    
    @NotNull(message = "Vehicle ID cannot be null")
    @JsonProperty("id")
    private String id;
    
    @NotNull(message = "Vehicle type cannot be null")
    @JsonProperty("type")
    private String type;
    
    @Positive(message = "Length must be positive")
    @JsonProperty("length")
    private Double length;
    
    @Positive(message = "Width must be positive")
    @JsonProperty("width")
    private Double width;
    
    @Positive(message = "Height must be positive")
    @JsonProperty("height")
    private Double height;
    
    public Vehicle() {
    }
    
    public Vehicle(String id, String type, Double length, Double width, Double height) {
        this.id = id;
        this.type = type;
        this.length = length;
        this.width = width;
        this.height = height;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Double getLength() {
        return length;
    }
    
    public void setLength(Double length) {
        this.length = length;
    }
    
    public Double getWidth() {
        return width;
    }
    
    public void setWidth(Double width) {
        this.width = width;
    }
    
    public Double getHeight() {
        return height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public double getVolume() {
        if (length == null || width == null || height == null) {
            return 0.0;
        }
        return length * width * height;
    }
    
    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
