package it.uniba.dib.sms22235.activities;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public interface ActivityInterface {
    FloatingActionButton getFab();
    FirebaseFirestore getFireStoreInstance();
    FirebaseStorage getStorageInstance();
    String getUserId();
}
