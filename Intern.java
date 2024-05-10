import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

public class Intern {
    private static ResourceBundle messages;

    public static void main(String[] args) {
        Object[] options = {"English", "Azerbaijani"};
        int n = JOptionPane.showOptionDialog(null, "Choose your language", "Language Selection",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        Locale locale;
        if (n == 1) {
            locale = new Locale("az", "AZ"); 
        } else {
            locale = new Locale("en", "US"); 
        }

        messages = ResourceBundle.getBundle("Messages", locale);
        new PersonalDatabaseGU(); 
    }

    public static String getMessage(String key) {
        return messages.getString(key);
    }
}

