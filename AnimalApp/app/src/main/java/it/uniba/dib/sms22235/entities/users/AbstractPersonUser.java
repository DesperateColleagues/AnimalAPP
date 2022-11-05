package it.uniba.dib.sms22235.entities.users;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractPersonUser {
    protected String name;
    protected String secondName;
    protected String email;
    protected String birthDate;
    private static final String EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

    public AbstractPersonUser() {}

    /**
     * @param name      nome della persona
     * @param secondName  cognome della persona
     * @param email     email della persona
     * @param birthDate data di nascita della persona
     *
     */
    public AbstractPersonUser(String name, String secondName, String email,
                              String birthDate) {

        this.name = name;
        this.secondName = secondName;
        this.email = email;
        this.birthDate = birthDate;
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

    public String getName() {
        return name;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthDate() {
        return birthDate;
    }
}
