package it.uniba.dib.sms22235.activities.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.registration.fragments.RegistrationOrganizationFragment;
import it.uniba.dib.sms22235.activities.registration.fragments.RegistrationPersonFragment;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.entities.users.Veterinarian;

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
    public void onPassionateRegistered(@NonNull Passionate passionate) {
        // First register the user with Firebase auth system
        // in order to authenticate him during login

        db.collection(KeysNamesUtils.CollectionsNames.ACTORS)
                .whereEqualTo(KeysNamesUtils.ActorFields.USERNAME, passionate.getUsername())
                .get()
                .addOnCompleteListener(taskCheckUsername -> {
                    // Create the new user only if the username is not duplicate
                    if (taskCheckUsername.isSuccessful() && taskCheckUsername.getResult().isEmpty()) {
                        // Create the AUTH user
                        mAuth.createUserWithEmailAndPassword(passionate.getEmail(), passionate.getPassword())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()){
                                        // Key of the document
                                        String docKey = KeysNamesUtils.RolesNames.COMMON_USER
                                                + "_" + passionate.getUsername();

                                        // Save the user instance on the DB
                                        db.collection(KeysNamesUtils.CollectionsNames.ACTORS)
                                                .document(docKey)
                                                .set(passionate)
                                                .addOnSuccessListener(unused -> {
                                                    // Show a message to let the user know
                                                    Toast.makeText(this, "Registrazione avvenuta con successo",
                                                                    Toast.LENGTH_LONG)
                                                            .show();

                                                    DataManipulationHelper.saveDataInternally(
                                                            this,
                                                            passionate,
                                                            KeysNamesUtils.FileDirsNames.currentPassionateOffline(passionate.getEmail()));

                                                    // Go to the profile fragment of the passionate
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE, passionate);
                                                    newActivityRunning(PassionateNavigationActivity.class, bundle);
                                                })
                                                .addOnFailureListener(e -> Log.d("DEB", e.getMessage()));

                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "Email già usata.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Toast.makeText(RegistrationActivity.this, "Username già usato.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onVeterinaryRegistered(@NonNull Veterinarian veterinarian) {
        // First register the veterinary with Firebase auth system
        // in order to authenticate him during login
        mAuth.createUserWithEmailAndPassword(veterinarian.getEmail(), veterinarian.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        // Save the veterinary instance on the DB
                        String docKey = KeysNamesUtils.RolesNames.VETERINARIAN
                                + "_" + veterinarian.getEmail();

                        db.collection(KeysNamesUtils.CollectionsNames.ACTORS)
                                .document(docKey)
                                .set(veterinarian)
                                // TODO: switch the activity to LoginActivity or DashboardActivity
                                .addOnSuccessListener(unused -> Log.d("REG", "Registrazione avvenuta con successo"))
                                .addOnFailureListener(e -> Log.d("DEB", e.getMessage()));
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Email già usata.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onOrganizationRegistered(@NonNull Organization org) {
        // First register the organization with Firebase auth system
        // in order to authenticate it during login
        mAuth.createUserWithEmailAndPassword(org.getEmail(), org.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        // Save the organization instance on the DB
                        String docKey = org.getPurpose()
                                + "_" + org.getEmail();

                        db.collection(KeysNamesUtils.CollectionsNames.ACTORS)
                                .document(docKey)
                                .set(org)
                                // TODO: switch the activity to LoginActivity or DashboardActivity
                                .addOnSuccessListener(unused -> Log.d("REG", "Registrazione avvenuta con successo"))
                                .addOnFailureListener(e -> Log.d("CrashOrg", e.getMessage()));

                    } else {
                        Toast.makeText(RegistrationActivity.this, "Email già usata.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * This method allows the app to switch, calling the activity
     *
     * @author Michelangelo De Pascale
     *
     * @param newActivityClass                 the class of the activity that we need to call
     * @param additionalData    optional bundle to pass as extra information to the newly
     *                          created activity
     */
    private void newActivityRunning(@SuppressWarnings("rawtypes") Class newActivityClass, Bundle additionalData){
        Intent intent = new Intent(this, newActivityClass);

        if (additionalData != null){
            intent.putExtras(additionalData);
        }

        startActivity(intent); //start a new activity
    }
}