package it.uniba.dib.sms22235.activities.passionate;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.fragments.ProfileFragment;
import it.uniba.dib.sms22235.activities.passionate.fragments.PurchaseFragment;

import it.uniba.dib.sms22235.database.QueryPurchasesManager;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class PassionateNavigationActivity extends AppCompatActivity implements ProfileFragment.ProfileFragmentListener, PurchaseFragment.PurchaseFragmentListener, Serializable {

    private transient FirebaseFirestore db;
    private transient QueryPurchasesManager queryPurchases;

    private transient Passionate passionate;
    private transient LinkedHashSet<Animal> animalSet;
    private transient ArrayList<Purchase> purchasesList;

    private transient FloatingActionButton fab;
    private transient BottomNavigationView navView;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_passionate_navigation);

        navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.passionate_profile, R.id.passionate_photo_diary,
                R.id.passionate_pet_care, R.id.passionate_purchase)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_passionate_navigation);

        assert navHostFragment != null;

        // Set up the navigation system
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        fab = findViewById(R.id.floatingActionButton);

        db = FirebaseFirestore.getInstance();

        queryPurchases = new QueryPurchasesManager(this);

        Bundle loginBundle = getIntent().getExtras(); // get the login bundle

        if (loginBundle != null) {
            passionate = (Passionate) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE);
            animalSet = (LinkedHashSet<Animal>) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_ANIMALS);
            purchasesList = (ArrayList<Purchase>) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_PURCHASES);
        }
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

    @Override
    public void onAnimalRegistered(@NonNull Animal animal) {
        String docKeyAnimal = KeysNamesUtils.RolesNames.ANIMAL
                + "_" + animal.getMicrochipCode();

        // Set the animal's owner
        animal.setOwner(passionate.getUsername());

        // Update the list of animals
        boolean isAdded = animalSet.add(animal);

        // Use the set to check the animal's uniqueness
        if (isAdded) {
            // Query to search if the microchip code is unique
            db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                    .document(docKeyAnimal)
                    .set(animal)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Animale registrato con successo",
                                    Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> Log.d("DEB", e.getMessage()));
        } else {
            Toast.makeText(this, "Animale duplicato: codice microchip già esistente",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPurchaseRegistered(@NonNull Purchase purchase) {

        purchase.setOwner(getPassionateUsername());

        purchasesList.add(purchase);

        long testValue = queryPurchases.insertPurchase(purchase.getAnimal(), purchase.getItemName(), purchase.getOwner(),
                purchase.getDate(), purchase.getCategory(), purchase.getCost(), purchase.getAmount());

        Toast.makeText(this, testValue + "", Toast.LENGTH_LONG).show();

        if (testValue != -1) {
            db.collection(KeysNamesUtils.CollectionsNames.PURCHASES)
                    .add(purchase)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Spesa salvata con successo",
                                Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(this, "Errore nell'inserimento della spesa", Toast.LENGTH_LONG).show();
        }
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
     * This method is used to get a copy of the set of
     * passionate's animal
     *
     * @return a copy of the set with passionate's animals
     * */
    public LinkedHashSet<Animal> getAnimalSet() {
        LinkedHashSet<Animal>clonedAnimalSet = new LinkedHashSet<>();

        for (Animal animal : animalSet){
            clonedAnimalSet.add((Animal) animal.clone());
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

        for (Purchase purchase : purchasesList) {
            clonedPurchasesList.add((Purchase) purchase.clone());
        }

        return clonedPurchasesList;
    }

    public void restoreBottomAppBarVisibility(){
        if (navView.getVisibility() == View.GONE && fab.getVisibility() == View.GONE) {
            navView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    public void setNavViewVisibility(int visibility) {
        navView.setVisibility(visibility);
    }

    public String getPassionateUsername() {
        return passionate.getUsername();
    }
}