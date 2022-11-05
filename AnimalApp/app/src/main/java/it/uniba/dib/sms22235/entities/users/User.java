package it.uniba.dib.sms22235.entities.users;

public class User extends AbstractPersonUser implements Owner {
    private String  username;

    public User(){}

    /**
     * @param name       name of the person
     * @param secondName second name of the person
     * @param email      email of the person used to perform operations like reset password
     * @param birthDate  birthday date
     * @param username   the username to access the private area of the app
     */
    public User(String name, String secondName, String email, String birthDate, String username) {
        super(name, secondName, email, birthDate);
        this.username = username;
    }

    @Override
    public String OwnerName() {
        return name + " " + secondName + " " + username;
    }

    public String getUsername() {
        return username;
    }
}
