package it.uniba.dib.sms22235.activities.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.utils.FirebaseNamesUtils;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.entities.users.User;
import it.uniba.dib.sms22235.entities.users.Veterinary;
import it.uniba.dib.sms22235.utils.FirestoreQueryHelper;

public class RegistrationActivity extends AppCompatActivity
        implements RegistrationPersonFragment.RegistrationPersonFragmentListener,
        RegistrationOrganizationFragment.RegistrationOrganizationFragmentListener {

    // Firebase objects to perform authentication and DB operations
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Get the instances of the Firebase's objects
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onUserRegistered(@NonNull User user, String pwd) {
        // First register the user with Firebase auth system
        // in order to authenticate him during login
        mAuth.createUserWithEmailAndPassword(user.getEmail(), pwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        // Save the user instance on the DB
                        String docKey = FirebaseNamesUtils.RolesNames.COMMON_USER
                                + "_" + user.getUsername();

                        db.collection(FirebaseNamesUtils.CollectionsNames.ACTORS)
                                .document(docKey)
                                .set(user)
                                // TODO: switch the activity to LoginActivity or DashboardActivity
                                .addOnSuccessListener(unused -> Log.d("REG", "Registrazione avvenuta con successo"))
                                .addOnFailureListener(e -> Log.d("DEB", e.getMessage()));
                    } else {
                        Log.d("DEB", Objects.requireNonNull(task.getException()).getMessage());
                    }
        });
    }

    @Override
    public void onVeterinaryRegistration(@NonNull Veterinary veterinary, String pwd) {
        // First register the veterinary with Firebase auth system
        // in order to authenticate him during login
        mAuth.createUserWithEmailAndPassword(veterinary.getEmail(), pwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        // Save the veterinary instance on the DB
                        String docKey = FirebaseNamesUtils.RolesNames.VETERINARY
                                + "_" + veterinary.getEmail();

                        db.collection(FirebaseNamesUtils.CollectionsNames.ACTORS)
                                .document(docKey)
                                .set(veterinary)
                                // TODO: switch the activity to LoginActivity or DashboardActivity
                                .addOnSuccessListener(unused -> Log.d("REG", "Registrazione avvenuta con successo"))
                                .addOnFailureListener(e -> Log.d("DEB", e.getMessage()));
                    } else {
                        Log.d("DEB", Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    @Override
    public void onOrganizationRegistered(@NonNull Organization org, String pwd) {
        // First register the organization with Firebase auth system
        // in order to authenticate it during login
        Toast.makeText(this, org.getEmail(), Toast.LENGTH_SHORT).show();
        mAuth.createUserWithEmailAndPassword(org.getEmail(), pwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        // Save the organization instance on the DB
                        String docKey = FirebaseNamesUtils.RolesNames.ORGANIZATION
                                + "_" + org.getEmail();

                        db.collection(FirebaseNamesUtils.CollectionsNames.ACTORS)
                                .document(docKey)
                                .set(org)
                                // TODO: switch the activity to LoginActivity or DashboardActivity
                                .addOnSuccessListener(unused -> Log.d("REG", "Registrazione avvenuta con successo"))
                                .addOnFailureListener(e -> Log.d("DEB", e.getMessage()));
                    } else {
                        Log.d("DEB", Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }
}