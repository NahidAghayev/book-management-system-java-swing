
/*


 * Created by Orkhan
 * 
 * 
 */

 package entities.user_and_admin;

 public final class Admin {
 
     /*
      * Admin's username and password will not be able to be changed
      * Maybe in the future Admin will work like that, it will require
      * special file with some code, and only program with that file
      * will be able to be Admin.
      * So, not everyone will be able to get Admin status.
      */
     static final private String ADMIN_NAME = "admin";
     static final private String ADMIN_PASSWORD = "admin";
 
     private Admin() {
 
     }
 
     // There will be only one Admin in the program.
     public static boolean login(String name, String password) {
         if (name.equals(ADMIN_NAME) && password.equals(ADMIN_PASSWORD)) {
             return true;
         }
         return false;
     }
    }