package it.uniba.dib.sms22235.activities.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.registration.RegistrationActivity;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.entities.users.Veterinary;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // firebase object to perform authentication
    private FirebaseFirestore db; // firebase object to perform DB operations

    // input fields for login
    private EditText txtInputEmail;
    private EditText txtInputPassword;
    private Button btnLogin;

    @Override
    protected void onResume() {
        super.onResume();
        btnLogin.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get an instance of the authentication object
        mAuth = FirebaseAuth.getInstance();

        // Get an instance of the database connection
        db = FirebaseFirestore.getInstance();

        // Retrieve input fields
        txtInputEmail = findViewById(R.id.txtInputOrgEmail);
        txtInputPassword = findViewById(R.id.txtInputOrgPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    /**
     * Specify what happens when the login action is performed
     * */
    public void loginAction(View view){
        String email = txtInputEmail.getText().toString();
        String password = txtInputPassword.getText().toString();

        if(!email.equals("") && !password.equals("")) {
            btnLogin.setEnabled(false);
            // Perform the sign in of the user with the retrieved email and password
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(taskLogin -> {
                        // Check if the login task is successful
                        if (taskLogin.isSuccessful()) {
                            // Get the Firebase logged user
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                /*
                                 *  Perform a query to find, in the actors collection, the logged user
                                 *  with the email provided by the FirebaseUser object.
                                 */
                                db.collection(KeysNamesUtils.CollectionsNames.ACTORS)
                                        .whereEqualTo("email", user.getEmail())
                                        .get()
                                        .addOnCompleteListener(taskGetUser -> {
                                            if (taskGetUser.isSuccessful()) {
                                                // Result of the query
                                                QuerySnapshot result = taskGetUser.getResult();

                                                /*
                                                 * Get the only document in the QuerySnapshot object.
                                                 * The reason why the retrieved document will always be one
                                                 * is that, there can't exist two users with the same email field.
                                                 * The only 'actors' document that will be found by the query,
                                                 * will be the one containing the logged user data.
                                                 * */
                                                DocumentSnapshot document = result.getDocuments().get(0);

                                                // Retrieve the document name to access the user role
                                                String docName = document.getId();

                                                // Get the role of the user by splitting the document name
                                                String role = docName.split("_")[0];

                                                /*
                                                 * Check what is the role of the logged user. Retrieve
                                                 * actors' data by the document and store them into the
                                                 * relative object.
                                                 * Start the correct activity by passing as Bundle the
                                                 * object of the logged actor
                                                 * */
                                                if (role.equals(KeysNamesUtils.RolesNames.COMMON_USER)) {
                                                    Passionate cus = Passionate.loadUserData(document);

                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE, cus);

                                                    // Execute the task to get the animals of the logged user
                                                    Task<QuerySnapshot> taskGetAnimals = db
                                                            .collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                                                            .whereEqualTo(KeysNamesUtils.AnimalFields.OWNER, cus.getUsername())
                                                            .get();

                                                    // Execute the task to get the purchases of the logged user
                                                    Task<QuerySnapshot> taskPurchases = db
                                                            .collection(KeysNamesUtils.CollectionsNames.PURCHASES)
                                                            .whereEqualTo(KeysNamesUtils.PurchaseFields.OWNER, cus.getUsername())
                                                            .get();

                                                    // Wait until every task is finished to fill the lists to pass
                                                    // as bundle to the PassionateNavigationActivity
                                                    Tasks.whenAllComplete(taskGetAnimals, taskPurchases).addOnCompleteListener(task -> {
                                                        // The order of the element in the task object follows the order
                                                        // of the task passed in input to the whenAllCompleteMethod
                                                        QuerySnapshot animalsSnapshot = (QuerySnapshot) task.getResult().get(0).getResult();
                                                        QuerySnapshot purchasesSnapshot = (QuerySnapshot) task.getResult().get(1).getResult();

                                                        LinkedHashSet<Animal> animals = new LinkedHashSet<>();
                                                        ArrayList<Purchase> purchases = new ArrayList<>();

                                                        // Check if the passionate has animals
                                                        if (!animalsSnapshot.isEmpty()) {
                                                            List<DocumentSnapshot> retrievedAnimalsDocuments = animalsSnapshot.getDocuments();

                                                            // Retrieve animals
                                                            for (DocumentSnapshot snapshot : retrievedAnimalsDocuments) {
                                                                animals.add(Animal.loadAnimal(snapshot));
                                                            }

                                                            // Check if the user passionate has purchases
                                                            if (!purchasesSnapshot.isEmpty()) {
                                                                List<DocumentSnapshot> retrievedPurchasesDocuments = purchasesSnapshot.getDocuments();

                                                                // Retrieve purchases
                                                                for (DocumentSnapshot snapshot : retrievedPurchasesDocuments) {
                                                                    purchases.add(Purchase.loadPurchase(snapshot));
                                                                }
                                                            }

                                                        }

                                                        // Fill the bundle. If one of the snapshot (or both) are empty
                                                        // the bundle will be filled with an empty array list, in order
                                                        // to prevent nullable errors
                                                        bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_ANIMALS, animals);
                                                        bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_PURCHASES, purchases);

                                                        // Start the new activity only once the bundle is filled
                                                        newActivityRunning(PassionateNavigationActivity.class, bundle);
                                                    });

                                                } else if (role.equals(KeysNamesUtils.RolesNames.VETERINARY)) {
                                                    Veterinary vet = Veterinary.loadVeterinary(document);
                                                } else if (role.equals(KeysNamesUtils.RolesNames.ORGANIZATION)) {
                                                    Organization org = Organization.loadOrganization(document);
                                                }
                                            }
                                        });
                                Toast.makeText(this, "Login corretto", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            btnLogin.setEnabled(true);
                            Toast.makeText(this, "Email o password non corretti", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Inserisci le tue credenziali per accedere",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Specify what happens when the user want to recover a lost password
     * */
    public void txtRecoverPasswordAction(View view){

    }

    /***
     * Start the RegistrationActivity
     * @param view the current view
     */
    public void btnRegisterAction(View view){
        // explicit intent to start the RegistrationActivity
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
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