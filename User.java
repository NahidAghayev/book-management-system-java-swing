public class User {
    private String username;
    private String password;
    private boolean isAdmin;

    // Constructor
    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // For security reasons, we won't expose getPassword()
    // Instead, we can implement a method to verify the password
    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
