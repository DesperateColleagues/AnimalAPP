package it.uniba.dib.sms22235.entities.users;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User extends AbstractPersonUser implements Serializable {
    protected String username;

    // need this constructor for FireBase
    public User(){}

    /**
     * @param fullName       name of the person
     * @param email      email of the person used to perform operations like reset password
     * @param username   the username to access the private area of the app
     */
    public User(String fullName, String email, String username) {
        super(fullName, email);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


    /**
     * This method is used to create a user object given the document stored in FirebaseFirestore
     *
     * @param document the document with user data
     * @return the new instance of the user
     * */
    @NonNull
    @Contract("_ -> new")
    public static User loadUserData(@NonNull DocumentSnapshot document){
        return new User(
                (String) document.get("name"),
                (String) document.get("email"),
                (String) document.get("username")
        );
    }
}
