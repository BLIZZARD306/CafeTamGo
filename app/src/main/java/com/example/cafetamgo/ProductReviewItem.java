package com.example.cafetamgo;

public class ProductReviewItem {
    private int reviewId;
    private int userId;
    private String reviewerName;
    private int rating;
    private String comment;
    private String reviewDate;

    public ProductReviewItem(int reviewId, int userId, String reviewerName, int rating, String comment, String reviewDate) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getUserId() {
        return userId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getReviewDate() {
        return reviewDate;
    }
}