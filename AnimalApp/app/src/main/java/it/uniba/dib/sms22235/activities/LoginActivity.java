package it.uniba.dib.sms22235.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.FireBaseNamesUtils;
import it.uniba.dib.sms22235.entities.users.User;
import it.uniba.dib.sms22235.entities.users.Veterinary;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // firebase object to perform authentication
    private FirebaseFirestore db; // firebase object to perform DB operations

    // input fields for login
    private EditText txtInputEmail;
    private EditText txtInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get an instance of the authentication object
        mAuth = FirebaseAuth.getInstance();

        // Get an instance of the database connection
        db = FirebaseFirestore.getInstance();

        // Retrieve input fields
        txtInputEmail = findViewById(R.id.txtInputEmail);
        txtInputPassword = findViewById(R.id.txtInputPassword);
    }

    /**
     * Specify what happens when the login action is performed
     * */
    public void loginAction(View view){
        String email = txtInputEmail.getText().toString();
        String password = txtInputPassword.getText().toString();

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
                            db.collection(FireBaseNamesUtils.CollectionsNames.ACTORS)
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

                                            // TODO: SWITCH ACTIVITY

                                            /*
                                            * Check what is the role of the logged user. Retrieve
                                            * actors' data by the document and store them into the
                                            * relative object.
                                            * Start the correct activity by passing as Bundle the
                                            * object of the logged actor
                                            * */
                                            if (role.equals(FireBaseNamesUtils.RolesNames.COMMON_USER)){
                                                User cus = User.loadUserData(document);
                                                Toast.makeText(this, cus.getUsername(), Toast.LENGTH_LONG).show();
                                            } else if (role.equals(FireBaseNamesUtils.RolesNames.VETERINARY)){
                                                Veterinary vet = Veterinary.loadVeterinary(document);
                                            } else if (role.equals(FireBaseNamesUtils.RolesNames.ORGANIZATION)) {
                                                // TODO: prepare organization object
                                            }
                                        }
                                    });
                            Toast.makeText(this, "Login corretto", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Specify what happens when the user want to recover a lost password
     * */
    public void recoverPassword(View view){

    }

}