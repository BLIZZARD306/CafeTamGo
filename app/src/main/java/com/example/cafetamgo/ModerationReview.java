package com.example.cafetamgo;

import java.io.Serializable;

public class ModerationReview implements Serializable {
    private int reviewId;
    private int menuItemId;
    private String foodName;
    private String stallName;
    private String reviewerName;
    private int rating;
    private String comment;
    private boolean visible;
    private int reportCount;
    private String reasons;
    private String explanation;

    public ModerationReview(int reviewId, int menuItemId, String foodName, String stallName,
                            String reviewerName, int rating, String comment, boolean visible,
                            int reportCount, String reasons, String explanation) {
        this.reviewId = reviewId;
        this.menuItemId = menuItemId;
        this.foodName = foodName;
        this.stallName = stallName;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
        this.visible = visible;
        this.reportCount = reportCount;
        this.reasons = reasons;
        this.explanation = explanation;
    }

    public int getReviewId() { return reviewId; }
    public int getMenuItemId() { return menuItemId; }
    public String getFoodName() { return foodName; }
    public String getStallName() { return stallName; }
    public String getReviewerName() { return reviewerName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public boolean isVisible() { return visible; }
    public int getReportCount() { return reportCount; }
    public String getReasons() { return reasons; }
    public String getExplanation() { return explanation; }
}