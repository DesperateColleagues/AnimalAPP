package it.uniba.dib.sms22235.utils;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreQueryHelper {

    private FirebaseFirestore db;

    /**
     * Set te instance of the Firestore
     *
     * @param db the db instance
     * */
    public FirestoreQueryHelper(FirebaseFirestore db) {
        this.db= db;
    }
    
    /**
     * This method is used to check if a string field value is unique
     * amongst the actor's collection, before the actor's document is saved.
     * For example it can be used to check email or username uniqueness inside the collection.
     *
     * @param fieldName the name of the field to check
     * @param value the value of the field whose uniqueness will be checked
     *
     * @return boolean value: true if the field value is unique, false if it is not
     * */
    public boolean checkStringFieldUniqueness(String fieldName, String value) {
        // TODO: implement this method with a whereEqualTo query on the db
        return true;
    }


}
