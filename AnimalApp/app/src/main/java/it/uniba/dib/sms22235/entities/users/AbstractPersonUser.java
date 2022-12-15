package it.uniba.dib.sms22235.entities.users;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractPersonUser implements Serializable {
    protected String fullName;
    protected String email;
    protected String password;
    private static final String EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

    public AbstractPersonUser() {}

    /**
     * @param fullName    full name (name + second name) of the person
     * @param email     email of the person
     * @param password the password of the user
     */
    public AbstractPersonUser(String fullName, String email, String password) {

        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    /**
     * Used to check email format
     *
     * @param email email to check
     *
     * @return boolean value that indicates the input email correctnes
     */
    boolean checkEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
