package it.uniba.dib.sms22235.tasks;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

/**
 * This interface determines the common operation of the NavigationActivities
 ** */
public interface ActivityInterface {
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
     * This method returns the id of the current logged user
     *
     * @return the id of the current logged user (email for organizations and veterinarians
     * and username for passionates)
     * */
    String getUserId();
}
