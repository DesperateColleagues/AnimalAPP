package it.uniba.dib.sms22235.tasks.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.organization.OrganizationNavigationActivity;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.registration.RegistrationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import it.uniba.dib.sms22235.database.QueryPurchasesManager;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.entities.users.Animal;

import it.uniba.dib.sms22235.utils.DataManipulationHelper;
import it.uniba.dib.sms22235.utils.InputFieldCheck;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.entities.users.Veterinarian;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // firebase object to perform authentication
    private FirebaseFirestore db; // firebase object to perform DB operations

    private QueryPurchasesManager manager;

    private boolean isConnectionEnabled;

    // input fields for login
    private EditText txtInputEmail;
    private EditText txtInputPassword;
    private Button btnLogin;

    // Callback that verifies the connectivity to a network at run time
    private final ConnectivityManager.NetworkCallback networkCallback =
            new ConnectivityManager.NetworkCallback() {

                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    isConnectionEnabled = true;
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    isConnectionEnabled = false;
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }
            };

    @Override
    protected void onResume() {
        super.onResume();
        btnLogin.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        manager = new QueryPurchasesManager(this);

        // Get an instance of the authentication object
        mAuth = FirebaseAuth.getInstance();

        // Get an instance of the database connection
        db = FirebaseFirestore.getInstance();

        // Retrieve input fields
        txtInputEmail = findViewById(R.id.txtInputOrgEmail);
        txtInputPassword = findViewById(R.id.txtInputOrgPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Build the network request
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        // Request the network from the ConnectivityManager and start listening
        // to the availability of the network from the networkCallback
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }

    /**
     * Specify what happens when the login action is performed
     * */
    public void loginAction(View view) throws NoSuchAlgorithmException {
        String email = txtInputEmail.getText().toString();
        String password = txtInputPassword.getText().toString();

        if(!email.equals("") && !password.equals("")) {
            btnLogin.setEnabled(false);

            // Perform the sign in of the user with the retrieved email and password
            if (isConnectionEnabled) {
                onlineLogin(email, InputFieldCheck.encodePassword(password));
            } else {
                offlineUserLogin(email, InputFieldCheck.encodePassword(password));
            }

        } else {
            Toast.makeText(this, "Inserisci le tue credenziali per accedere",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method is used to perform an online login to the app. The authentication is made
     * using Firebase authentication system. The user data will be loaded and passed to the correct
     * Activity, that will be started if the process is successful
     *
     * @param email the email of the user
     * @param password the password of the user
     * */
    private void onlineLogin(String email, String password) {
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

                                            Log.wtf("Role",role);

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
                                                Task<QuerySnapshot> taskGetAnimals = getAnimalTask(cus.getEmail());
                                                // Execute the task to get the purchases of the logged user
                                                Task<QuerySnapshot> taskPurchases = getPurchasesTask(cus.getUsername());
                                                Task<QuerySnapshot> taskAvailableReservations = getAvailableReservationsTask();
                                                Task<QuerySnapshot> taskPassionateReservations = getPassionateReservationsTask(cus.getUsername());
                                                Task<QuerySnapshot> taskVeterinarians = getVeterinariansTask();

                                                // Wait until every task is finished to fill the lists to pass
                                                // as bundle to the PassionateNavigationActivity
                                                Tasks.whenAllComplete(
                                                        taskGetAnimals,
                                                        taskPurchases,
                                                        taskAvailableReservations,
                                                        taskPassionateReservations,
                                                        taskVeterinarians
                                                ).addOnCompleteListener(task -> {

                                                    if (task.isSuccessful()) {
                                                        // Refresh local data structures if the connection is enabled and the task completed
                                                        manager.dropTableAndRefresh();
                                                        deleteFile(KeysNamesUtils.FileDirsNames.localAnimalsSet(email));

                                                        // The order of the element in the task object follows the order
                                                        // of the task passed in input to the whenAllCompleteMethod
                                                        QuerySnapshot animalsSnapshot = (QuerySnapshot) task.getResult().get(0).getResult();
                                                        QuerySnapshot purchasesSnapshot = (QuerySnapshot) task.getResult().get(1).getResult();
                                                        QuerySnapshot availableReservationsSnapshot = (QuerySnapshot) task.getResult().get(2).getResult();
                                                        QuerySnapshot passionateReservationsSnapshot = (QuerySnapshot) task.getResult().get(3).getResult();
                                                        QuerySnapshot veterinariansSnapshot = (QuerySnapshot) task.getResult().get(4).getResult();

                                                        LinkedHashSet<Animal> animals = new LinkedHashSet<>();
                                                        ArrayList<Purchase> purchases = new ArrayList<>();
                                                        ArrayList<Reservation> availableReservations = new ArrayList<>();
                                                        ArrayList<Reservation> passionateReservations = new ArrayList<>();
                                                        ArrayList<Veterinarian> veterinarians = new ArrayList<>();

                                                        // Check if the passionate has animals
                                                        if (!animalsSnapshot.isEmpty()) {
                                                            List<DocumentSnapshot> retrievedAnimalsDocuments = animalsSnapshot.getDocuments();

                                                            // Retrieve animals
                                                            for (DocumentSnapshot snapshot : retrievedAnimalsDocuments) {
                                                                animals.add(Animal.loadAnimal(snapshot));
                                                            }

                                                            // Create back the local set file with the updated data from fire store
                                                            DataManipulationHelper.saveDataInternally(this, animals,
                                                                    KeysNamesUtils.FileDirsNames.localAnimalsSet(email));

                                                            // Check if the user passionate has purchases
                                                            if (!purchasesSnapshot.isEmpty()) {
                                                                List<DocumentSnapshot> retrievedPurchasesDocuments = purchasesSnapshot.getDocuments();

                                                                // Retrieve purchases
                                                                for (DocumentSnapshot snapshot : retrievedPurchasesDocuments) {
                                                                    Purchase purchase = Purchase.loadPurchase(snapshot);

                                                                    // Update the local DB with the changes in purchases' table
                                                                    manager.insertPurchase(
                                                                            purchase.getAnimal(),
                                                                            purchase.getItemName(),
                                                                            purchase.getOwner(),
                                                                            purchase.getDate(),
                                                                            purchase.getCategory(),
                                                                            purchase.getCost(),
                                                                            purchase.getAmount()
                                                                    );
                                                                    purchases.add(purchase);
                                                                }
                                                            }

                                                            if (!availableReservationsSnapshot.isEmpty()) {
                                                                List<DocumentSnapshot> availableReservationsDocuments = availableReservationsSnapshot.getDocuments();

                                                                for (DocumentSnapshot snapshot : availableReservationsDocuments) {
                                                                    Reservation reservation = Reservation.loadReservation(snapshot);

                                                                    availableReservations.add(reservation);
                                                                }
                                                            }
                                                            if (!passionateReservationsSnapshot.isEmpty()) {
                                                                List<DocumentSnapshot> passionateReservationsDocuments = passionateReservationsSnapshot.getDocuments();

                                                                for (DocumentSnapshot snapshot : passionateReservationsDocuments) {
                                                                    Reservation reservation = Reservation.loadReservation(snapshot);

                                                                    passionateReservations.add(reservation);
                                                                }
                                                            }
                                                        }

                                                        if(!veterinariansSnapshot.isEmpty()){
                                                            List<DocumentSnapshot> veterinariansDocuments = veterinariansSnapshot.getDocuments();

                                                            for (DocumentSnapshot snapshot : veterinariansDocuments) {
                                                                veterinarians.add(Veterinarian.loadVeterinarian(snapshot));
                                                            }
                                                        }
                                                        // Fill the bundle. If one of the snapshot (or both) are empty
                                                        // the bundle will be filled with an empty array list, in order
                                                        // to prevent nullable errors
                                                        bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_ANIMALS, animals);
                                                        bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_PURCHASES, purchases);
                                                        bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_RESERVATIONS, passionateReservations);
                                                        bundle.putSerializable(KeysNamesUtils.BundleKeys.AVAILABLE_RESERVATIONS, availableReservations);
                                                        bundle.putSerializable(KeysNamesUtils.BundleKeys.VETERINARIANS_LIST, veterinarians);

                                                        // Start the new activity only once the bundle is filled
                                                        newActivityRunning(PassionateNavigationActivity.class, bundle);

                                                    }
                                                }).addOnFailureListener(e -> offlineUserLogin(email, password));

                                            } else if (role.equals(KeysNamesUtils.RolesNames.VETERINARIAN)) {
                                                Veterinarian vet = Veterinarian.loadVeterinarian(document);

                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable(KeysNamesUtils.BundleKeys.VETERINARIAN, vet);

                                                Task<QuerySnapshot> taskGetReservations = db
                                                        .collection(KeysNamesUtils.CollectionsNames.RESERVATIONS)
                                                        .whereEqualTo(KeysNamesUtils.ReservationFields.VETERINARIAN, vet.getEmail())
                                                        .get();

                                                Tasks.whenAllComplete(taskGetReservations).addOnCompleteListener(task -> {
                                                    QuerySnapshot reservationsSnapshot = (QuerySnapshot) task.getResult().get(0).getResult();

                                                    ArrayList<Reservation> reservations = new ArrayList<>();

                                                    if (!reservationsSnapshot.isEmpty()) {
                                                        List<DocumentSnapshot> retrievedReservationsDocuments = reservationsSnapshot.getDocuments();

                                                        for (DocumentSnapshot snapshot : retrievedReservationsDocuments) {
                                                            reservations.add(Reservation.loadReservation(snapshot));
                                                        }
                                                    }

                                                    bundle.putSerializable(KeysNamesUtils.BundleKeys.VETERINARIAN_RESERVATIONS, reservations);

                                                    // Start the new activity only once the bundle is filled
                                                    newActivityRunning(VeterinarianNavigationActivity.class, bundle);
                                                });

                                            } else if (role.equals(KeysNamesUtils.RolesNames.PUBLIC_ORGANIZATION) || role.equals(KeysNamesUtils.RolesNames.PRIVATE_ORGANIZATION)) {
                                                Bundle bundle = new Bundle();
                                                Organization org = Organization.loadOrganization(document);
                                                bundle.putSerializable(KeysNamesUtils.BundleKeys.ORGANIZATION, org);

                                                ArrayList<Veterinarian> veterinarians = new ArrayList<>();
                                                Task<QuerySnapshot> taskVeterinarians = getVeterinariansTask();
                                                taskVeterinarians.addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        QuerySnapshot veterinariansSnapshot = (QuerySnapshot) task.getResult();
                                                        if(!veterinariansSnapshot.isEmpty()){
                                                            List<DocumentSnapshot> veterinariansDocuments = veterinariansSnapshot.getDocuments();
                                                            for (DocumentSnapshot snapshot : veterinariansDocuments) {
                                                                veterinarians.add(Veterinarian.loadVeterinarian(snapshot));
                                                            }
                                                        }
                                                    }
                                                    bundle.putSerializable(KeysNamesUtils.BundleKeys.VETERINARIANS_LIST, veterinarians);
                                                    newActivityRunning(OrganizationNavigationActivity.class, bundle);
                                                }
                                                );
                                            }
                                        }
                                    });
                            Toast.makeText(this, "Login corretto", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        btnLogin.setEnabled(true);
                        Toast.makeText(this, "Email o password non corretti o rete non abilitata",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void offlineUserLogin(String email, String password) {
        Passionate passionate = (Passionate)
                DataManipulationHelper.readDataInternally(this,
                        KeysNamesUtils.FileDirsNames.currentPassionateOffline(email));

        if (passionate != null) {
            // Check if email and password are correct
            if (passionate.getEmail().equals(email) && passionate.getPassword().equals(password)) {
                ArrayList<Purchase> purchasesLocalList = new ArrayList<>();

                // Retrieve the local animals
                LinkedHashSet<Animal> animalLocalLinkedHashSet = (LinkedHashSet<Animal>)
                        DataManipulationHelper.readDataInternally(this,
                                KeysNamesUtils.FileDirsNames.localAnimalsSet(email));

                if (animalLocalLinkedHashSet != null) {
                    Cursor cursor = manager.runFilterQuery(passionate.getUsername(), null,
                            null,  null, "", "");

                    if (cursor != null) {
                        if (cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                // Retrieve the purchase using Cursor
                                Purchase purchase = new Purchase(
                                        cursor.getString(cursor.getColumnIndexOrThrow(
                                                KeysNamesUtils.PurchaseFields.ANIMAL)),

                                        cursor.getString(cursor.getColumnIndexOrThrow(
                                                KeysNamesUtils.PurchaseFields.ITEM_NAME)),

                                        cursor.getString(cursor.getColumnIndexOrThrow(
                                                KeysNamesUtils.PurchaseFields.DATE)),

                                        cursor.getString(cursor.getColumnIndexOrThrow(
                                                KeysNamesUtils.PurchaseFields.CATEGORY)),

                                        cursor.getFloat(cursor.getColumnIndexOrThrow(
                                                KeysNamesUtils.PurchaseFields.COST)),

                                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                                KeysNamesUtils.PurchaseFields.AMOUNT))
                                );

                                purchase.setOwner(cursor.getString(cursor.getColumnIndexOrThrow(
                                        KeysNamesUtils.PurchaseFields.OWNER)));

                                purchasesLocalList.add(purchase);
                            }
                        }
                    }
                }

                Bundle bundle = new Bundle();

                // Fill the bundle to pass to the activity
                bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE, passionate);
                bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_ANIMALS, animalLocalLinkedHashSet);
                bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_PURCHASES, purchasesLocalList);

                newActivityRunning(PassionateNavigationActivity.class, bundle);
            }
        }
    }

    /**
     * Specify what happens when the user want to recover a lost password
     * */
    public void txtRecoverPasswordAction(View view){
        // todo
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

    @NonNull
    private Task<QuerySnapshot> getAnimalTask(String passionateUsername){
        return db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.OWNER, passionateUsername)
                .get();
    }

    @NonNull
    private Task<QuerySnapshot> getVeterinariansTask() {
        return db.collection(KeysNamesUtils.CollectionsNames.ACTORS)
                .whereEqualTo(KeysNamesUtils.ActorFields.PURPOSE, KeysNamesUtils.RolesNames.VETERINARIAN)
                .get();
    }

    @NonNull
    private Task<QuerySnapshot> getPassionateReservationsTask(String passionateUsername) {
        return db.collection(KeysNamesUtils.CollectionsNames.RESERVATIONS)
                .whereEqualTo(KeysNamesUtils.ReservationFields.OWNER, passionateUsername)
                .get();
    }

    @NonNull
    private Task<QuerySnapshot> getAvailableReservationsTask() {
        return db.collection(KeysNamesUtils.CollectionsNames.RESERVATIONS)
                .whereEqualTo(KeysNamesUtils.ReservationFields.OWNER, null)
                .whereEqualTo(KeysNamesUtils.ReservationFields.ANIMAL, null)
                .get();
    }

    @NonNull
    private Task<QuerySnapshot> getPurchasesTask(String passionateUsername) {
        return db.collection(KeysNamesUtils.CollectionsNames.PURCHASES)
                .whereEqualTo(KeysNamesUtils.PurchaseFields.OWNER, passionateUsername)
                .get();
    }
}