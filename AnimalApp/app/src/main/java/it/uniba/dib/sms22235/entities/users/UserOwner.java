package it.uniba.dib.sms22235.entities.users;

import java.time.LocalDate;

public class UserOwner extends User implements Owner {

    /**
     * @param name       name of the person
     * @param secondName second name of the person
     * @param email      email of the person used to perform operations like reset password
     * @param birthDate  birthday date
     * @param username   the username to access the private area of the app
     */
    public UserOwner(String name, String secondName, String email, String birthDate, String username) {
        super(name, secondName, email, birthDate, username);
    }
}
