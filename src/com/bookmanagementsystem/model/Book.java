package com.bookmanagementsystem.model;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private String title;
    private String author;
    private List<Integer> ratings;
    private List<String> reviews;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.ratings = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public void addRating(int rating) {
        ratings.add(rating);
    }

    public double calculateAverageRating() {
        if (ratings.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (int rating : ratings) {
            sum += rating;
        }
        return (double) sum / ratings.size();
    }

    public void addReview(String review) {
        reviews.add(review);
    }

    public String generateReviews() {
        if (reviews.isEmpty()) {
            return "No review";
        }
        StringBuilder sb = new StringBuilder();
        for (String review : reviews) {
            sb.append(review).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Integer> getRatings() {
        return ratings;
    }

    public void setRatings(List<Integer> ratings) {
        this.ratings = ratings;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }
}
