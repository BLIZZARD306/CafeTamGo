package com.example.cafetamgo;

public class MenuItem {
    private int menuItemId;
    private int stallId;
    private String foodName;
    private String description;
    private String category;
    private int price; // Stored in centavos (e.g., 8000 for ₱80.00)
    private String imageUri;
    private int isAvailable;
    private String stallName; // Joined from stalls table for recommendations
    private double averageRating;
    private int reviewCount;

    public MenuItem(int menuItemId, int stallId, String foodName, String description, String category, int price, String imageUri, int isAvailable, String stallName, double averageRating, int reviewCount) {
        this.menuItemId = menuItemId;
        this.stallId = stallId;
        this.foodName = foodName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.imageUri = imageUri;
        this.isAvailable = isAvailable;
        this.stallName = stallName;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    public int getMenuItemId() { return menuItemId; }
    public int getStallId() { return stallId; }
    public String getFoodName() { return foodName; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public int getPrice() { return price; }
    public String getImageUri() { return imageUri; }
    public int getIsAvailable() { return isAvailable; }
    public String getStallName() { return stallName; }
    public double getAverageRating() { return averageRating; }
    public int getReviewCount() { return reviewCount; }
}