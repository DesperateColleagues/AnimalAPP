package it.uniba.dib.sms22235.entities.users;

public class UserOwner extends User implements Owner {

    /**
     * @param name       name of the person
     * @param email      email of the person used to perform operations like reset password
     * @param username   the username to access the private area of the app
     */
    public UserOwner(String name, String secondName, String email, String birthDate, String username) {
        super(name, email, username);
    }

    @Override
    public String OwnerName() {
        return username;
    }
}
