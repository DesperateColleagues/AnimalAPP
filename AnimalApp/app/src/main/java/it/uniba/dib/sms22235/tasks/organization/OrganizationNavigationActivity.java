package it.uniba.dib.sms22235.tasks.organization;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.tasks.organization.fragments.OrganizationImportDataFragment;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class OrganizationNavigationActivity extends AppCompatActivity implements
        OrganizationImportDataFragment.OrganizationImportDataFragmentListener {

    private FloatingActionButton fab;
    private transient Organization organization;
    private transient FirebaseFirestore db;

    // Flag that specify whether the connection is enabled or not
    private boolean isConnectionEnabled;

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

                    Toast.makeText(getApplicationContext(),
                            "Connessione persa: avvio modalità offline.\nAlcune funzionalità possono non essere più disponibili",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organization_navigation);

        BottomNavigationView navViewVeterinarian = findViewById(R.id.nav_view_org);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.organization_profile,
                R.id.organization_import_data
        ).build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_organization_navigation);

        assert navHostFragment != null;

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navViewVeterinarian, navController);

        Bundle loginBundle = getIntent().getExtras(); // get the login bundle

        // Extract the bundle data sent from login activity
        if (loginBundle != null) {
            organization = (Organization) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.ORGANIZATION);
        }

        fab = findViewById(R.id.floatingActionButton_organization);

        db = FirebaseFirestore.getInstance();

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

        public FloatingActionButton getFab() {
        return fab;
    }

    @Override
    public void onAnimalRegistered(@NonNull Animal animal) {
        if (isConnectionEnabled) {
            // Set the animal's owner
            animal.setOwner(organization.getEmail());
            registerAnimalFirebase(animal);
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
                                        // Update the local animal's files
                                        /*DataManipulationHelper.saveDataInternally(this, animalSet,
                                                KeysNamesUtils.FileDirsNames.localAnimalsSet(passionate.getEmail()));*/
                                    });
                        }
                    }
                });
    }


}
