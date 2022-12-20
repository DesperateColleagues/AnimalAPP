package it.uniba.dib.sms22235.entities.users;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Passionate extends AbstractPersonUser implements Serializable {
    protected String username;

    // need this constructor for FireBase
    public Passionate(){}

    /**
     * @param fullName       name of the person
     * @param email      email of the person used to perform operations like reset password
     * @param username   the username to access the private area of the app
     */
    public Passionate(String fullName, String email, String username, String password) {
        super(fullName, email, password);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


    /**
     * This method is used to create a user object
     * given the document stored in FirebaseFirestore
     *
     * @param document the document with user data
     * @return the new instance of the user
     * */
    @NonNull
    @Contract("_ -> new")
    public static Passionate loadUserData(@NonNull DocumentSnapshot document){
        return new Passionate(
                (String) document.get(KeysNamesUtils.ActorFields.FULL_NAME),
                (String) document.get(KeysNamesUtils.ActorFields.EMAIL),
                (String) document.get(KeysNamesUtils.ActorFields.USERNAME),
                (String) document.get(KeysNamesUtils.ActorFields.PASSWORD)
        );
    }
}
