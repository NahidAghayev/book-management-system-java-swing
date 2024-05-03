
/*


 * Created by Orkhan
 * 
 * 
 */

 
 public class Parameteres {
     /*
      * current_user-status will hold mode of program ADMIN or USER
      * it will depend how user is logined
      * For example Admin can login by entering username: "admin", and password:
      * "admin",
      * current_user_status will be ADMIN
      */
     private static Status current_user_status;
 
     /*
      * This object will hold the user who is active in the program
      * so when user will make changes all data will be made on active_user
      */
     private static User active_user;
 
     // Location of data folder
     public static final String USER_PATH = "./data/users/";
     public static final String USER_BOOK_PATH = "./data/users/added_books/";
     public static final String USER_REVIEW_PATH = "./data/users/reviews/";
     public static final String USER_RATING_PATH = "./data/users/ratings/";
     public static final String BOOK_PATH = "./data/books/";
     public static final String BOOK_REVIEW_PATH = "./data/books/reviews/";
     public static final String REVIEW_PATH = "./data/reviews/";
     public static final String RATING_PATH = "./data/ratings/";
     public static final String SETTINGS_PATH = "./program_settings/settings.txt";
     public static final String FILE_FORMAT = ".txt";
 
     /*************************** */
 
     public static Status getUserStatus() {
         return current_user_status;
     }
 
     public static void setUserStatus(Status s) {
         current_user_status = s;
     }
 
     public static User getActiveUser() {
         return active_user;
     }
 
     public static void setActiveUser(User u) {
         active_user = u;
     }
 }