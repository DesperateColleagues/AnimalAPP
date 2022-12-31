package it.uniba.dib.sms22235.activities.passionate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Collections;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.fragments.PassionateProfileFragment;
import it.uniba.dib.sms22235.activities.passionate.fragments.PassionateReservationFragment;

import it.uniba.dib.sms22235.activities.passionate.fragments.animalprofile.PhotoDiaryFragment;
import it.uniba.dib.sms22235.activities.passionate.fragments.PurchaseFragment;

import it.uniba.dib.sms22235.adapters.PostGridAdapter;
import it.uniba.dib.sms22235.database.QueryPurchasesManager;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class PassionateNavigationActivity extends AppCompatActivity implements
        PassionateProfileFragment.ProfileFragmentListener, PurchaseFragment.PurchaseFragmentListener,
        PassionateReservationFragment.PassionateReservationFragmentListener,
        PhotoDiaryFragment.PhotoDiaryFragmentListener,
        Serializable {

    private transient FirebaseFirestore db;
    private transient QueryPurchasesManager queryPurchases;

    private transient Passionate passionate;
    private transient LinkedHashSet<Animal> animalSet;
    private transient ArrayList<Purchase> purchasesList;
    private transient ArrayList<Reservation> passionateReservationsList;
    private transient ArrayList<Reservation> availableReservationsList;
    private transient ArrayList<Veterinarian> veterinariansList;

    private transient FloatingActionButton fab;
    private transient BottomNavigationView navView;

    private transient NavHostFragment navHostFragment;

    // Flag that specify whether the connection is enabled or not
    private boolean isConnectionEnabled;

    // Callback that verifies the connectivity to a network at run time
    private final ConnectivityManager.NetworkCallback networkCallback =
            new ConnectivityManager.NetworkCallback() {

        @SuppressWarnings("unchecked")
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);

            isConnectionEnabled = true;

            ArrayList<Purchase> purchasesOfflineList = (ArrayList<Purchase>)
                    DataManipulationHelper.readDataInternally(PassionateNavigationActivity.this,
                            KeysNamesUtils.FileDirsNames.ADD_PURCHASE);

            // Purchases offline operations
            if (purchasesOfflineList != null) {

                // Upload the offline files to fire store
                for (Purchase purchase : purchasesOfflineList) {
                    registerPurchaseFirebase(purchase);
                }

                // todo: check safe delete

                // Delete the file once the process is over. If some network error occurs
                // during onPurchaseRegistered then a new offline file will be created
                // and will contain all the purchases that didn't make the upload to fire store
                deleteFile(KeysNamesUtils.FileDirsNames.ADD_PURCHASE);
            }
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);

            isConnectionEnabled = false;

            Toast.makeText(PassionateNavigationActivity.this,
                    "Connessione persa: avvio modalità offline.\nAlcune funzionalità possono non essere più disponibili",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_passionate_navigation);

        navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.passionate_profile, R.id.passionate_pet_care, R.id.passionate_purchase)
                .build();

        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_passionate_navigation);

        assert navHostFragment != null;

        // Set up the navigation system
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Use this method to not bug the app with undesired animation
        navView.setOnNavigationItemSelectedListener(item -> {
            final int PASSIONATE_PROFILE = R.id.passionate_profile;
            final int PASSIONATE_PET_CARE = R.id.passionate_pet_care;
            final int PASSIONATE_PURCHASE = R.id.passionate_purchase;

            switch (item.getItemId()) {
                case PASSIONATE_PROFILE:
                    navController.navigate(R.id.passionate_profile);
                    break;
                    case PASSIONATE_PET_CARE:
                    navController.navigate(R.id.passionate_pet_care);
                    break;
                case PASSIONATE_PURCHASE:
                    navController.navigate(R.id.passionate_purchase);
                    break;
            }
            return true;
        });

        fab = findViewById(R.id.floatingActionButton);

        db = FirebaseFirestore.getInstance();

        queryPurchases = new QueryPurchasesManager(this);

        Bundle loginBundle = getIntent().getExtras(); // get the login bundle

        // Extract the bundle data sent from login activity
        if (loginBundle != null) {
            passionate = (Passionate) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE);
            animalSet = (LinkedHashSet<Animal>) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_ANIMALS);
            purchasesList = (ArrayList<Purchase>) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_PURCHASES);

            passionateReservationsList = (ArrayList<Reservation>) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_RESERVATIONS);
            availableReservationsList = (ArrayList<Reservation>) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.AVAILABLE_RESERVATIONS);
            veterinariansList = (ArrayList<Veterinarian>) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.VETERINARIANS_LIST);
        }

        // Init the animal data set if it is null
        if (animalSet == null) {
            animalSet = new LinkedHashSet<>();
        }

        // Init the purchases data set it is null
        if (purchasesList == null) {
            purchasesList = new ArrayList<>();
        }

        // Init the available reservations data set it is null
        if (passionateReservationsList == null) {
            passionateReservationsList = new ArrayList<>();
        }

        // Init the passionate reservations data set it is null
        if (availableReservationsList == null) {
            availableReservationsList = new ArrayList<>();
        }

        // Init the purchases data set it is null
        if (veterinariansList == null) {
            veterinariansList = new ArrayList<>();
        }

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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        restoreBottomAppBarVisibility();
    }

    // SECTION: ENTITY REGISTRATION MANAGEMENT

    @Override
    public void onAnimalRegistered(@NonNull Animal animal) {
        if (isConnectionEnabled) {
            // Set the animal's owner
            animal.setOwner(passionate.getUsername());

            // Update the list of animals
            boolean isAdded = animalSet.add(animal);

            // Use the set to check the animal's uniqueness
            if (isAdded) {
                    registerAnimalFirebase(animal);

            } else {
                Toast.makeText(this, "Animale duplicato: codice microchip già esistente",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Impossibile salvare l'animale: rete assente",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPurchaseRegistered(@NonNull Purchase purchase) {

        purchase.setOwner(getPassionateUsername());

        purchasesList.add(purchase);

        long testValue = queryPurchases.insertPurchase(
                purchase.getAnimal(), purchase.getItemName(),
                purchase.getOwner(), purchase.getDate(), purchase.getCategory(),
                purchase.getCost(), purchase.getAmount());

        if (testValue != -1) {
            // Save the purchase only if there is an internet connection
            if (isConnectionEnabled) {
                registerPurchaseFirebase(purchase);
            } else {
                // Read the current offline purchase additions
                ArrayList<Purchase> purchasesOfflineList = (ArrayList<Purchase>)
                        DataManipulationHelper.readDataInternally(this,
                        KeysNamesUtils.FileDirsNames.ADD_PURCHASE);

                if (purchasesOfflineList == null) {
                    purchasesOfflineList = new ArrayList<>();
                }

                purchasesOfflineList.add(purchase);

                // Update the files which holds the purchases' addition while the app is offline
                DataManipulationHelper.saveDataInternally(this, purchasesOfflineList,
                        KeysNamesUtils.FileDirsNames.ADD_PURCHASE);
            }
        } else {
            Toast.makeText(this, "Errore nell'inserimento della spesa", Toast.LENGTH_LONG).show();
        }
    }

    // SECTION: POST MANAGEMENT

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void loadPost(PostGridAdapter adapter, List<PhotoDiaryPost> postsList, String animal) {
        //  todo: parametrize the animal

        db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                .whereEqualTo(KeysNamesUtils.PhotoDiaryFields.POST_ANIMAL, animal)
                .addSnapshotListener((value, error) -> {

                    // Handle the error if the listening is not working
                    if (error != null) {
                        Log.w("Error listen", "listen:error", error);
                        return;
                    }

                    if (value != null) {
                        // Check for every document
                        for (DocumentChange change : value.getDocumentChanges()) {
                            PhotoDiaryPost post = PhotoDiaryPost.loadPhotoDiaryPost(change.getDocument());
                            postsList.add(post);
                        }

                        // Notify the changes on the adapter
                        Collections.reverse(postsList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onPostAdded(@NonNull PhotoDiaryPost post) {
        String fileName = System.currentTimeMillis() + "";

        // Create the storage tree structure
        String fileReference = KeysNamesUtils.FileDirsNames.passionatePostDirName(getPassionateUsername()) +
                "/" +
                KeysNamesUtils.FileDirsNames.passionatePostRefDirAnimal(post.getPostAnimal())
                + "/" + fileName;

        // Get a reference of the storage by passing the tree structure
        StorageReference storageReference = FirebaseStorage.getInstance().getReference
                (fileReference);

        // Give to the user a feedback to wait
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Salvando l'immagine...");
        progressDialog.show();

        // Start the upload task
        UploadTask uploadTask = storageReference.putFile(Uri.parse(post.getPostUri()));

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult()
                        .getStorage().getDownloadUrl().addOnCompleteListener(taskUri -> {
                            // Set the download post URI
                            post.setPostUri(taskUri.getResult().toString());

                            // Save the post into the FireStore
                            db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                                    .add(post).addOnSuccessListener(documentReference -> {
                                        Toast.makeText(PassionateNavigationActivity.this,
                                                "Caricamento completato con successo", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    });
                        });
            }
        });
    }

    //method to ask permissions
    // todo: improve permissions requests
    public void requestPermission() {
        String permissionRead = Manifest.permission.READ_EXTERNAL_STORAGE;
        String permissionWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        int grantRead = ContextCompat.checkSelfPermission(this,permissionRead);
        int grantWrite = ContextCompat.checkSelfPermission(this, permissionWrite);

        String [] permissions = {permissionRead,permissionWrite};

        if(grantRead != PackageManager.PERMISSION_GRANTED || grantWrite != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissionRead) && ActivityCompat.shouldShowRequestPermissionRationale(this, permissionWrite)) {
                //TODO Dialog
            } else {
                int STORAGE_REQUEST_CODE = 1;
                ActivityCompat.requestPermissions(this,permissions, STORAGE_REQUEST_CODE);
            }
        }
    }

    /**
     * This method is used to register an animal into the Firebase firestore
     *
     * @param animal the animal to be registered
     * */
    private void registerAnimalFirebase(@NonNull Animal animal) {
        String docKeyAnimal = KeysNamesUtils.RolesNames.ANIMAL
                + "_" + animal.getMicrochipCode();

        // Check if the microchip code is duplicated first
        db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.MICROCHIP_CODE, animal.getMicrochipCode())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If no animal own that microchip code, then it is possible to insert
                        if (task.getResult().isEmpty()) {
                            db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                                    .document(docKeyAnimal)
                                    .set(animal)
                                    .addOnSuccessListener(unused -> {

                                        Toast.makeText(this,
                                                "Animale registrato con successo",
                                                Toast.LENGTH_LONG).show();

                                        // Update the local animal's files
                                        DataManipulationHelper.saveDataInternally(this, animalSet,
                                                KeysNamesUtils.FileDirsNames.localAnimalsSet(passionate.getEmail()));
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Animale duplicato: " +
                                                            "codice microchip già esistente",
                                                    Toast.LENGTH_SHORT).show());
                        }
                    }
                });
    }

    /**
     * This method is used to register a purchase into Firebase firestore
     *
     * @param purchase the purchase to be registered
     * */
    private void registerPurchaseFirebase(Purchase purchase) {
        db.collection(KeysNamesUtils.CollectionsNames.PURCHASES)
                .add(purchase)
                .addOnSuccessListener(documentReference -> Toast.makeText(this,
                        "Spesa salvata con successo",
                        Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> {
                    // Add changes to local files
                });
    }

    /**
     * This method is used to get a copy of the set of
     * passionate's animal
     *
     * @return a copy of the set with passionate's animals
     * */
    public LinkedHashSet<Animal> getAnimalSet() {
        LinkedHashSet<Animal> clonedAnimalSet = new LinkedHashSet<>();

        if (animalSet != null) {
            for (Animal animal : animalSet) {
                clonedAnimalSet.add((Animal) animal.clone());
            }
        }

        return clonedAnimalSet;
    }

    /**
     * This method is used to get a copy of passionate's purchases list
     *
     * @return a copy of the set with passionate's purchases
     * */
    public ArrayList<Purchase> getPurchasesList() {
        ArrayList<Purchase> clonedPurchasesList = new ArrayList<>();

        if (purchasesList != null) {
            for (Purchase purchase : purchasesList) {
                clonedPurchasesList.add((Purchase) purchase.clone());
            }
        }

        return clonedPurchasesList;
    }

    /**
     * This method is used to get a copy of passionate's reservations list
     *
     * @return a copy of the list with passionate's reservations
     * */
    public ArrayList<Reservation> getPassionateReservationsList() {
        ArrayList<Reservation> clonedPassionateReservationsList = new ArrayList<>();

        if (passionateReservationsList != null) {
            for (Reservation reservation : passionateReservationsList) {
                clonedPassionateReservationsList.add((Reservation) reservation.clone());
            }
        }

        return clonedPassionateReservationsList;
    }

    /**
     * This method is used to get a copy of available reservations list
     *
     * @return a copy of the list with available reservations
     * */
    public ArrayList<Reservation> getAvailableReservationsList(String date) {
        ArrayList<Reservation> clonedReservationsList = new ArrayList<>();

        for (Reservation reservation : availableReservationsList) {
            //Log.wtf("Date, i guess", "" + date + " - " + reservation.getDate());
            if (date.equals(reservation.getDate())){
                clonedReservationsList.add((Reservation) reservation.clone());
            }
        }

        return clonedReservationsList;
    }
    
     /* This method is used to restore the visibility at the bottom app bar
     */

    public void restoreBottomAppBarVisibility(){
        if (navView.getVisibility() == View.GONE && fab.getVisibility() == View.GONE) {
            navView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method is used to modify the visibility of the bottom app bar
     *
     * @param visibility the visibility value
     * */
    public void setNavViewVisibility(int visibility) {
        navView.setVisibility(visibility);
    }

    /**
     * This method is used to obtain the global fab used to add new data according
     * to the current selected fragment
     *
     * @return an instance of the global fab
     * */
    public FloatingActionButton getFab() {
        return fab;
    }

    /**
     * This method is used to obtain the username of the current logged passionate
     *
     * @return the username
     * */
    public String getPassionateUsername() {
        return passionate.getUsername();
    }

    @Override
    public void onReservationBooked(@NonNull Reservation reservation) {
        String dateFormatted = reservation.getDate();
        String timeFormatted = reservation.getTime();

        availableReservationsList.remove(reservation);
        reservation.setOwner(getPassionateUsername());

        String docKeyReservation = KeysNamesUtils.CollectionsNames.RESERVATIONS
                +"_"+ dateFormatted.replaceAll("[-+^/]*", "")
                +"_"+ timeFormatted;

        db.collection(KeysNamesUtils.CollectionsNames.RESERVATIONS)
                .whereEqualTo(KeysNamesUtils.ReservationFields.DATE, reservation.getDate())
                .whereEqualTo(KeysNamesUtils.ReservationFields.TIME, reservation.getTime())
                .whereEqualTo(KeysNamesUtils.ReservationFields.VETERINARIAN, reservation.getVeterinarian())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()){
                            db.collection(KeysNamesUtils.CollectionsNames.RESERVATIONS)
                                    .document(docKeyReservation)
                                    .set(reservation)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Prenotazione confermata!", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> {
                                        Log.d("DEB", e.getMessage());
                                    });
                        } else {
                            Toast.makeText(this, "Si è verificato un errore, provare più tardi.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public List<Animal> getAnimalsByVeterinarian(String veterinarian) {
        List<Animal> animalsByVeterinarian = new ArrayList<>();
        for (Animal animal : animalSet) {
            if (veterinarian.equals(animal.getVeterinarian())) {
                animalsByVeterinarian.add((Animal) animal.clone());
            }
        }

        return animalsByVeterinarian;
    }

    @Override
    public List<Veterinarian> getVeterinarianList() {
        List<Veterinarian> clonedVeterinarianList = new ArrayList<>();

        for (Veterinarian veterinarian : veterinariansList) {
            clonedVeterinarianList.add((Veterinarian) veterinarian.clone());
        }

        return clonedVeterinarianList;
    }

    @Override
    public void onAnimalUpdated(@NonNull Animal animal) {
        if (isConnectionEnabled) {
            updateAnimalVeterinarian(animal);
        } else {
            Toast.makeText(this, "Impossibile modificare l'animale: rete assente",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void updateAnimalVeterinarian(@NonNull Animal animal) {

        String docKeyAnimal = KeysNamesUtils.RolesNames.ANIMAL
                + "_" + animal.getMicrochipCode();

        db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.MICROCHIP_CODE, animal.getMicrochipCode())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                                    .document(docKeyAnimal)
                                    .update(KeysNamesUtils.AnimalFields.VETERINARIAN,animal.getVeterinarian())
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this,
                                                "Animale aggiornato con successo",
                                                Toast.LENGTH_LONG).show();

                                        // Update the local animal's files
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Errore interno, dati non aggiornati",
                                                    Toast.LENGTH_SHORT).show());
                        }
                    }
                });
    }


}