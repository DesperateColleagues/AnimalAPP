package it.uniba.dib.sms22235.tasks;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import it.uniba.dib.sms22235.entities.users.AbstractPersonUser;

/**
 * This interface determines the common operation of the NavigationActivities
 ** */
public interface NavigationActivityInterface {
    /**
     * This method is used to obtain an instance of the global fab in order to attach listeners
     *
     * @return an instance of the fab
     * */
    FloatingActionButton getFab();

    /**
     * This method returns an instance of the FireStore database instantiated int the activity
     *
     * @return the db reference
     * */
    FirebaseFirestore getFireStoreInstance();

    /**
     * This method returns an instance of the Firebase storage instantiated int the activity
     *
     * @return the storage
     * */
    FirebaseStorage getStorageInstance();


    /**
     * This method returns an instance of the auth object instantiated int the activity
     *
     * @return the auth instance
     * */
    FirebaseAuth getAuthInstance();

    /**
     * This method returns the id of the current logged user
     *
     * @return the id of the current logged user (email for organizations and veterinarians
     * and username for passionates)
     * */
    String getUserId();

    /**
     * This method is used to modify the visibility of the bottom app bar
     *
     * @param visibility the visibility value
     * */
    void setNavViewVisibility(int visibility);


    /**
     * This method is used to restore the normal visibility to the nav bar
     * */
    void restoreBottomAppBarVisibility();

    /**
     * This method return the user as person
     *
     * @return the user abstraction
     * */
    AbstractPersonUser getUser();

}
