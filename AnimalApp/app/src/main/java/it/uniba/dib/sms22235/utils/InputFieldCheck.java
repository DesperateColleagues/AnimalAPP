package it.uniba.dib.sms22235.utils;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * This class is used to check input fields with regex
 * */
public class InputFieldCheck {

    public static int MIN_CHAR = 6;

    /**
     * This method verifies if an email is valid or no
     *
     * @param email the email to check
     *
     * @return boolean value
     * */
    public static boolean isEmailValid(String email) {
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:.[a-zA-Z0-9-]+)*$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(email).matches();
    }

    /**
     * This method verifies is a phone number is valid
     *
     * @param number the number
     *
     * @return boolean value
     * */
    public static boolean isNumberValid(String number)
    {

        String regex = "^\\+[1-9]{1}[0-9]{3,14}$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(number).matches();
    }

    @Contract(pure = true)
    public static  boolean isPasswordValid(@NonNull String password){
        return password.length() >= MIN_CHAR;
    }

    /**
     * This method is used to encode a string, using the MD5 algorithm
     *
     * @param encodingString      the string to encode
     *
     * @return the encoded string
     * */
    @NonNull
    public static String encodePassword(@NonNull String encodingString) throws NoSuchAlgorithmException {
        // MessageDigest instance for MD5
        final String MD5_MODE = "MD5";
        MessageDigest md = MessageDigest.getInstance(MD5_MODE);

        // Update MessageDigest with input text in bytes
        md.update(encodingString.getBytes(StandardCharsets.UTF_8));

        // Get the hash bytes
        byte[] hashBytes = md.digest();

        //Convert hash bytes to hex format
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
