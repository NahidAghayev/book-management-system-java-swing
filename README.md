# Book Management System - User Interface

This Java Swing application provides a user-friendly interface for managing books in a library or bookstore. Users can register, log in, and access various functionalities such as browsing books, borrowing, and returning books.

## Features

- User registration and authentication
- Book browsing and searching
- Borrowing and returning books
- Admin privileges for managing books and users

## Dependencies

This application relies on the following components:

- Java Swing: For creating the graphical user interface.
- Java I/O: For reading from and writing to files.
- `User` class: Defines the structure and behavior of user objects.
- `Book` class: Represents a book with attributes such as title, author, and availability status.

## Class Structure

### User Class

This class represents a user entity with attributes such as username, password, and isAdmin status.

#### Attributes:
- `username`: A string representing the username of the user.
- `password`: A string representing the password of the user.
- `isAdmin`: A boolean indicating whether the user is an administrator or not.

#### Constructor:
- `User(String username, String password, boolean isAdmin)`: Initializes a new User object with the given username, password, and isAdmin status.

#### Methods:
- `getUsername()`: Returns the username of the user.
- `setUsername(String username)`: Sets the username of the user.
- `getPassword()`: Returns the password of the user. Note: For security reasons, this method is not exposed.
- `verifyPassword(String password)`: Verifies whether the given password matches the user's password. Returns `true` if the passwords match, otherwise `false`.
- `isAdmin()`: Returns `true` if the user is an administrator, otherwise `false`.
- `setAdmin(boolean isAdmin)`: Sets the isAdmin status of the user.


### Book Class

This class represents a book entity with attributes such as title, author, ratings, and reviews.

#### Attributes:
- `title`: A string representing the title of the book.
- `author`: A string representing the author of the book.
- `ratings`: A list of integers representing the ratings given to the book.
- `reviews`: A list of strings representing the reviews written for the book.

#### Constructor:
- `Book(String title, String author)`: Initializes a new Book object with the given title and author. Ratings and reviews are initialized as empty lists.

#### Methods:
- `addRating(int rating)`: Adds a rating for the book.
- `calculateAverageRating()`: Calculates and returns the average rating of the book.
- `addReview(String review)`: Adds a review for the book.
- `generateReviews()`: Generates and returns a string containing all reviews for the book.
- `getTitle()`: Returns the title of the book.
- `setTitle(String title)`: Sets the title of the book.
- `getAuthor()`: Returns the author of the book.
- `setAuthor(String author)`: Sets the author of the book.
- `getRatings()`: Returns the list of ratings for the book.
- `setRatings(List<Integer> ratings)`: Sets the list of ratings for the book.
- `getReviews()`: Returns the list of reviews for the book.
- `setReviews(List<String> reviews)`: Sets the list of reviews for the book.


### MyGUI Class

This class represents a graphical user interface (GUI) for a login/registration form. It allows users to register new accounts, login with existing accounts, and provides access to different functionalities based on user roles.

#### Attributes:
- `DATABASE_FILE`: A constant string representing the file path of the user database (CSV format).
- `ADMIN_USERNAME`: A constant string representing the username of the admin account.
- `ADMIN_PASSWORD`: A constant string representing the password of the admin account.
- `usersMap`: A map to store usernames and corresponding `User` objects.

#### Constructor:
- `MyGUI()`: Initializes the GUI components, loads existing users from the CSV database, and sets up event listeners for login and registration actions.

#### Methods:
- `loadUsersFromCSV()`: Loads existing users from the CSV database file into memory and returns a map of usernames and `User` objects.
- `saveUsersToCSV()`: Saves the current state of `usersMap` to the CSV database file.
- `isPasswordStrong(String password)`: Checks if a password meets the strength criteria (at least 8 characters long, contains at least one uppercase letter, one lowercase letter, and one digit).
- `main(String[] args)`: Entry point of the application, initializes and displays the GUI.


### GeneralDatabaseGUI Class Overview

The `GeneralDatabaseGUI` class provides a graphical user interface for managing books. It includes the following functionalities:

#### Constructor
- Initializes the GUI components.
- Reads book data from a CSV file.
- Populates the table with the read data.
- Calculates average ratings for the books.

#### Read Data from CSV
- Reads book data from a CSV file.
- Populates the table with the read data.

#### Calculate Average Rating for Book
- Calculates the average rating for each book based on user ratings stored in another CSV file.

#### Load Reviews for Book
- Loads reviews for a specific book from a CSV file containing user reviews.

#### Custom Cell Renderer
- Customizes the appearance of the review column to make usernames clickable.

#### Get User Rating for Book
- Retrieves a user's rating for a specific book from a CSV file.

#### Get User Review for Book
- Retrieves a user's review for a specific book from a CSV file.

#### Show User Details
- Displays details about a user's interaction with a book in a separate window.

#### Calculate Average Ratings
- Recalculates average ratings for all books in the table and updates the table accordingly.

#### Load User Ratings
- Loads a user's ratings from a CSV file.

#### Save Data to General CSV
- Saves the data displayed in the table to a general CSV file, updating the average ratings in the process.

#### Update Rating in Personal CSV
- Updates a book's rating in a CSV file containing user ratings.

#### Add to Library
- Allows the user to add selected books to their library, checking for duplicates in the process.

#### Load User Book History
- Loads a user's book history from a CSV file.

#### Filter Books
- Filters the displayed books based on a search query.

#### Show All Books
- Displays all books in the table.

#### Update Table
- Updates the table with a new set of books.

### General Database of Admin

The **General Database of Admin** is a Java Swing application designed to provide administrative functionalities for managing a book management system. This system enables administrators to perform various tasks related to titles, users, and user reviews, facilitating efficient management of the book database.

#### Features

1. **Title Management**: Administrators can view, delete, and search for titles within the system. The application provides an intuitive interface for managing book titles.

2. **User Management**: The system allows administrators to view and delete user accounts. Administrators can monitor user activity and manage user accounts as necessary.

3. **User Review Management**: Administrators can view, delete, and search user reviews. The application provides insights into user feedback, allowing administrators to maintain the quality of reviews.

4. **Search Functionality**: Administrators can search for titles based on title or author name. The search feature enhances usability by enabling quick access to specific titles.

5. **Back Navigation**: Administrators can easily navigate back to the login page using the provided "Back" button. This feature ensures smooth user experience and seamless navigation within the application.

#### Usage

1. **Title Management**:
   - View Titles: Navigate to the "Titles" tab to view a list of titles along with their authors.
   - Delete Titles: Select one or more titles and click the "Delete Selected Titles" button to remove them from the system.
   - Search Titles: Enter a search query in the search field located at the top-right corner to filter titles based on the query.

2. **User Management**:
   - View Users: Switch to the "Users" tab to see a list of user accounts.
   - Delete Users: Select a user from the list and click the "Delete Selected User" button to delete the user account.

3. **User Review Management**:
   - View Reviews: Access the "User Reviews" tab to view user reviews for various titles.
   - Delete Reviews: Select one or more reviews and click the "Delete Selected Reviews" button to remove them from the system.

4. **Search Functionality**:
   - Enter a search query in the search field located at the top-right corner of the application window and click the "Search" button to filter titles based on the query.

5. **Back Navigation**:
   - Click the "Back" button located at the top-left corner of the application window to return to the login page.


### Personal Database GUI

The `PersonalDatabaseGUI` class provides a graphical user interface (GUI) for managing personal book data. This class extends `JFrame` and allows users to add, delete, and save their book entries.

#### Class Overview

#### Dependencies
- **Swing Components**: Utilizes Swing components for GUI development.
- **DefaultTableModel**: Manages the data structure for the table.
- **File I/O**: Handles reading from and writing to files.
- **Scanner**: Reads data from files.
- **Calendar**: Manages date and time operations.

#### Constructor
- **Parameters**: 
  - `selectedBooks`: ArrayList of selected books to be added to the personal database.
  - `userBookHistory`: ArrayList of user's book history.
  - `username`: Username of the current user.
- **Title**: Sets the title of the frame to "Add to library".
- **Size and Position**: Sets the size of the frame to 800x400 pixels and centers it on the screen.

#### Components
- **Username Label**: Displays the username of the current user at the top of the frame.
- **Back Button**: Allows the user to navigate back to the general database view.
- **Table**: Displays the book data with editable columns for user rating, spend time, start date, end date, and status.
- **Save Button**: Saves the data entered by the user to a personal CSV file.

#### Inner Classes
- **NonEditableCellRenderer**: Renders specific columns non-editable.
- **RatingCellEditor**: Custom cell editor for the "User Rating" column.
- **TimeCellEditor**: Custom cell editor for the "Spend Time (minutes)" column.
- **DateCellEditor**: Custom cell editor for the "Start Date" and "End Date" columns.
- **ButtonRenderer**: Renders the delete button in the last column.
- **ButtonEditor**: Allows users to delete book entries by clicking the delete button.

#### Usage

1. **Data Entry**: Enter book details such as rating, spend time, start date, and end date in the corresponding columns.
2. **Delete Entry**: Click the delete button in the last column of the row to delete a book entry.
3. **Save Data**: Click the "Save" button to save the entered data to a personal CSV file.

#### Notes

- Ensure all required fields are filled before saving the data.
- Date format should be "yyyy-MM-dd" (e.g., 2022-05-30).
- Ratings must be between 0 and 5.
- Time spent should be a non-negative integer.


# Filter and Sort Function

The `FilterAndSortFunction` class provides functionality for sorting and filtering data in a `JTable` component. It includes methods for sorting data based on column headers and maintaining the state of sorting for each column.

## Class Overview

### Dependencies
- **Swing Components**: Utilizes Swing components for GUI development.
- **DefaultTableModel**: Manages the data structure for the table.
- **MouseAdapter**: Handles mouse events.
- **Comparator**: Compares objects for sorting purposes.
- **Map**: Stores the sorting state for each column.

### SortSelected Method
- **Parameters**: 
  - `table`: The `JTable` component to be sorted.
- **Description**: Initializes the table and adds a mouse listener to the table header for sorting functionality.

### SortMouseListener Inner Class
- **Description**: Implements the `MouseListener` interface to handle mouse click events on the table header for sorting.

### MultiColumnComparator Inner Class
- **Description**: Implements the `Comparator` interface to compare objects for multi-column sorting based on the sorting states of each column.

## Usage

1. **Initialization**: Call the `SortSelected` method and pass the `JTable` component to enable sorting.
2. **Sorting**: Click on the column headers to sort the data in ascending, descending, or original order.

## Notes

- Ensure the table model is set to `DefaultTableModel`.
- Data in the table must be comparable for sorting to work correctly.
- Clicking on a column header toggles between ascending, descending, and original order.


# Main Class

The `Main` class serves as the entry point for the application. It initializes and displays the main graphical user interface (GUI) by invoking the `MyGUI` class within the `SwingUtilities.invokeLater` method.

## Class Overview

### Dependencies
- **SwingUtilities**: Provides a utility class for invoking methods on the Swing event dispatching thread.

### Main Method
- **Description**: Entry point of the application. Initializes and displays the main GUI by creating an instance of the `MyGUI` class within the `SwingUtilities.invokeLater` method.

## Usage

1. **Execution**: Execute the `main` method to start the application.
2. **GUI Display**: The main GUI window will be displayed, allowing users to interact with the application.

## Notes

- It is recommended to encapsulate GUI initialization within the `invokeLater` method to ensure it is executed on the Swing event dispatching thread for thread safety.
