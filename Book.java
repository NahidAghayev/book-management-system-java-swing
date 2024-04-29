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

    // Method to add a rating for the book
    public void addRating(int rating) {
        ratings.add(rating);
    }

    // Method to calculate the average rating of the book
    public double calculateAverageRating() {
        if (ratings.isEmpty()) {
            return 0; // No rating
        }
        int sum = 0;
        for (int rating : ratings) {
            sum += rating;
        }
        return (double) sum / ratings.size();
    }

    // Method to add a review for the book
    public void addReview(String review) {
        reviews.add(review);
    }

    // Method to generate reviews based on user ratings
    public String generateReviews() {
        if (reviews.isEmpty()) {
            return "No review";
        }
        StringBuilder sb = new StringBuilder();
        for (String review : reviews) {
            sb.append(review).append(", ");
        }
        return sb.substring(0, sb.length() - 2); // Remove the last comma and space
    }

    // Getters and Setters
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
