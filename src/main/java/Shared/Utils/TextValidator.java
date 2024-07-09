package Shared.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextValidator {
    public static boolean validateEmail(String emailStr) {
        Matcher matcher = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(emailStr);
        return matcher.matches();
    }


    public static String validatePassword(String password) {
        String errorMessage = "";
        if(password.length() < 8)
        {
            errorMessage += "\n   * Should contain at least 8 characters";
        }

        Matcher matcher = Pattern.compile("[a-z]").matcher(password);
        //matcher.matches() this one considers the whole string,and want it to be like the patter
        if(!matcher.find())
        {
            errorMessage += "\n   * Should contain a lower case letter";
        }

        matcher = Pattern.compile("[A-Z]").matcher(password);
        if(!matcher.find())
        {
            errorMessage += "\n   * Should contain an upper case letter";
        }
        matcher = Pattern.compile("[0-9]").matcher(password);
        if(!matcher.find())
        {
            errorMessage += "\n   * Should contain at least a number";
        }

        matcher = Pattern.compile("[!@#$%^&*]").matcher(password);
        if(!matcher.find())
        {
            errorMessage += "\n   * Should contain at least a symbol !@#$%^&*";
        }

        if(!errorMessage.isBlank())
        {
            return "Some password requirements are not met:\n" + errorMessage.trim();
        }
        return errorMessage;
    }
}
