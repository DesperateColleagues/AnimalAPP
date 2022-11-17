package it.uniba.dib.sms22235.utils;

import java.util.regex.Pattern;

public class InputFieldCheck {

    public static int MIN_CHAR = 6;

    public static boolean isEmailValid(String email) {
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:.[a-zA-Z0-9-]+)*$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(email).matches();
    }

    public static boolean isNumberValid(String number)
    {

        String regex = "^\\+[1-9]{1}[0-9]{3,14}$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(number).matches();
    }

    public static  boolean isPasswordValid(String password){
        return password.length() >= MIN_CHAR;
    }
}
