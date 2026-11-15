package com.example.cafetamgo;

public class Stall {
    private int stallId;
    private String stallName;
    private String location;
    private String openingTime;
    private String closingTime;
    private String description;
    private int isOpen;
    private double averageRating;
    private int reviewCount;

    public Stall(int stallId, String stallName, String location, String openingTime, String closingTime, String description, int isOpen, double averageRating, int reviewCount) {
        this.stallId = stallId;
        this.stallName = stallName;
        this.location = location;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.description = description;
        this.isOpen = isOpen;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    public int getStallId() { return stallId; }
    public String getStallName() { return stallName; }
    public String getLocation() { return location; }
    public String getOpeningTime() { return openingTime; }
    public String getClosingTime() { return closingTime; }
    public String getDescription() { return description; }
    public int getIsOpen() { return isOpen; }
    public double getAverageRating() { return averageRating; }
    public int getReviewCount() { return reviewCount; }
}