package it.uniba.dib.sms22235.tasks.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.organization.OrganizationNavigationActivity;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.registration.fragments.RegistrationOrganizationFragment;
import it.uniba.dib.sms22235.tasks.registration.fragments.RegistrationPersonFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
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

        // Give to the user a feedback to wait
        ProgressDialog progressDialog = new ProgressDialog(this,R.style.Widget_App_ProgressDialog);
        progressDialog.setMessage(getString(R.string.salvando_appassionato));
        progressDialog.show();

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
                                                    Toast.makeText(this,
                                                                    getString(R.string.registrazione_successo),
                                                                    Toast.LENGTH_LONG)
                                                            .show();

                                                    DataManipulationHelper.saveDataInternally(
                                                            this,
                                                            passionate,
                                                            KeysNamesUtils.FileDirsNames.currentPassionateOffline(passionate.getEmail()));

                                                    // Go to the profile fragment of the passionate
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE, passionate);
                                                    bundle.putBoolean(KeysNamesUtils.BundleKeys.ONLINE, true);

                                                    progressDialog.dismiss();

                                                    newActivityRunning(PassionateNavigationActivity.class, bundle);
                                                })
                                                .addOnFailureListener(e -> Log.d("DEB", e.getMessage()));

                                    } else {
                                        Toast.makeText(RegistrationActivity.this, getString(R.string.error_email_usata),
                                                Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });

                    } else {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.error_username_usato),
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onVeterinaryRegistered(@NonNull Veterinarian veterinarian) {
        // First register the veterinary with Firebase auth system
        // in order to authenticate him during login
        // Give to the user a feedback to wait
        ProgressDialog progressDialog = new ProgressDialog(this,R.style.Widget_App_ProgressDialog);
        progressDialog.setMessage(getString(R.string.salvando_veterinario));
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(veterinarian.getEmail(), veterinarian.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        // Save the veterinary instance on the DB
                        String docKey = KeysNamesUtils.RolesNames.VETERINARIAN
                                + "_" + veterinarian.getEmail();

                        db.collection(KeysNamesUtils.CollectionsNames.ACTORS)
                                .document(docKey)
                                .set(veterinarian)
                                .addOnSuccessListener(unused -> {
                                    Log.d("AnimalAPP - Registrazione", "RegistrationActivity:131 - " + getString(R.string.registrazione_successo));
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(KeysNamesUtils.BundleKeys.VETERINARIAN, veterinarian);
                                    progressDialog.dismiss();
                                    newActivityRunning(VeterinarianNavigationActivity.class, bundle);
                                })
                                .addOnFailureListener(e -> progressDialog.dismiss());
                    } else {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.error_email_usata), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onOrganizationRegistered(@NonNull Organization org) {
        // First register the organization with Firebase auth system
        // in order to authenticate it during login
        ProgressDialog progressDialog = new ProgressDialog(this,R.style.Widget_App_ProgressDialog);
        progressDialog.setMessage(getString(R.string.registrando_ente));
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(org.getEmail(), org.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        // Save the organization instance on the DB
                        String docKey = org.getPurpose()
                                + "_" + org.getEmail();

                        db.collection(KeysNamesUtils.CollectionsNames.ACTORS)
                                .document(docKey)
                                .set(org)
                                .addOnSuccessListener(unused -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(KeysNamesUtils.BundleKeys.ORGANIZATION, org);
                                    progressDialog.dismiss();
                                    newActivityRunning(OrganizationNavigationActivity.class, bundle);
                                })
                                .addOnFailureListener(e -> progressDialog.dismiss());

                    } else {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.error_email_usata), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
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