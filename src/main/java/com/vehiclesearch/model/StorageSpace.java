package com.vehiclesearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a storage space from listings.json
 */
public class StorageSpace {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("length")
    private Double length;
    
    @JsonProperty("width")
    private Double width;
    
    @JsonProperty("height")
    private Double height;
    
    @JsonProperty("pricePerMonth")
    private Double pricePerMonth;
    
    @JsonProperty("location")
    private String location;
    
    @JsonProperty("features")
    private List<String> features;
    
    public StorageSpace() {
    }
    
    public StorageSpace(String id, String type, Double length, Double width, Double height,
                       Double pricePerMonth, String location, List<String> features) {
        this.id = id;
        this.type = type;
        this.length = length;
        this.width = width;
        this.height = height;
        this.pricePerMonth = pricePerMonth;
        this.location = location;
        this.features = features;
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
    
    public Double getPricePerMonth() {
        return pricePerMonth;
    }
    
    public void setPricePerMonth(Double pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public List<String> getFeatures() {
        return features;
    }
    
    public void setFeatures(List<String> features) {
        this.features = features;
    }
    
    public double getVolume() {
        if (length == null || width == null || height == null) {
            return 0.0;
        }
        return length * width * height;
    }
    
    public boolean canFit(Vehicle vehicle) {
        if (vehicle == null || this.length == null || this.width == null || this.height == null
            || vehicle.getLength() == null || vehicle.getWidth() == null || vehicle.getHeight() == null) {
            return false;
        }
        return this.length >= vehicle.getLength() 
            && this.width >= vehicle.getWidth() 
            && this.height >= vehicle.getHeight();
    }
    
    @Override
    public String toString() {
        return "StorageSpace{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", pricePerMonth=" + pricePerMonth +
                ", location='" + location + '\'' +
                '}';
    }
}
