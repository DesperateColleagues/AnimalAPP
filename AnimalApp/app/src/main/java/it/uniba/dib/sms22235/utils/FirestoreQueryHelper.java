package it.uniba.dib.sms22235.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.atomic.AtomicReference;

public class FirestoreQueryHelper {

    private FirebaseFirestore db;
    private QuerySnapshot snapshot ;


    /**
     * Set te instance of the Firestore
     *
     * @param db the db instance
     * */
    public FirestoreQueryHelper(FirebaseFirestore db) {
        this.db= db;
        snapshot = null;
    }
    
    /**
     * This method is used to check if a string field value is unique
     * amongst the actor's collection, before the actor's document is saved.
     * For example it can be used to check email or username uniqueness inside the collection.
     *
     * @param collection the name of the collection
     * @param fieldName the name of the field to check
     * @param value the value of the field whose uniqueness will be checked
     *
     * @return boolean value: true if the field value is unique, false if it is not
     * */
    public boolean checkStringFieldUniqueness(String collection, String fieldName, String value) {

        db.collection(collection).whereEqualTo(fieldName, value).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    }
                });

        return snapshot.isEmpty();
    }


}
