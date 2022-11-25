package it.uniba.dib.sms22235.activities.passionate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.fragments.ProfileFragment;
import it.uniba.dib.sms22235.adapters.AnimalListAdapter;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class PassionateNavigationActivity extends AppCompatActivity implements /*DialogAddAnimalFragment.DialogAddAnimalFragmentListener,*/ ProfileFragment.ProfileFragmentListener {

    private FirebaseFirestore db;
    private Passionate passionate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_passionate_navigation);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.passionate_profile, R.id.passionate_pet_care, R.id.passionate_purchase)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_passionate_navigation);

        assert navHostFragment != null;

        // Set up the navigation system
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        db = FirebaseFirestore.getInstance();

        Bundle loginBundle = getIntent().getExtras(); // get the login bundle

        if (loginBundle != null) {
            passionate = (Passionate) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE);
        }

        // Find the profile fragment from the navigation
        ProfileFragment profileFragment = (ProfileFragment) navHostFragment.getChildFragmentManager()
                .getPrimaryNavigationFragment();

        if (profileFragment != null) {
            // Set the Fab action from the fragment in order to better manager callbacks
            profileFragment.setFabAction(fab);
        }
    }

    @Override
    public void retrieveUserAnimals(RecyclerView recyclerView) {
        AnimalListAdapter adapter = new AnimalListAdapter();

        db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.OWNER, passionate.getUsername())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                            for (DocumentSnapshot document : documents) {
                                // Add the animal to the list by loading it with the static method
                                adapter.addAnimal(Animal.loadAnimal(document));
                            }

                            // Set up the recycler view to show the retrieved animals
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(
                                    this, RecyclerView.HORIZONTAL, false));
                        }
                    }
                });
    }

    @Override
    public void onAnimalRegistered(@NonNull Animal animal, RecyclerView recyclerView) {
        String docKeyAnimal = KeysNamesUtils.RolesNames.ANIMAL
                + "_" + animal.getMicrochipCode();

        // Set the animal's owner
        animal.setOwner(passionate.getUsername());

        // Query to search if the microchip code is unique
        db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.MICROCHIP_CODE, animal.getMicrochipCode())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            // Insert animal data into Firestore
                            db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                                    .document(docKeyAnimal)
                                    .set(animal)
                                    .addOnSuccessListener(unused -> {
                                        // Obtain adapter from the recycle
                                        AnimalListAdapter adapter = (AnimalListAdapter) recyclerView.getAdapter();

                                        // Check the adapter nullability: if it is null
                                        // then it's the first time an animal is registered for
                                        // this user. The recycle view must be initialized.
                                        // Otherwise it is only necessary to update the adapter
                                        // And notify the recycle view.
                                        if (adapter != null) {
                                            adapter.addAnimal(animal);
                                            adapter.notifyItemInserted(adapter.getItemCount() - 1);
                                        } else {
                                            adapter = new AnimalListAdapter();
                                            adapter.addAnimal(animal);

                                            recyclerView.setAdapter(adapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(
                                                    this, RecyclerView.HORIZONTAL, false));
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.d("DEB", e.getMessage()));
                        } else {
                            Toast.makeText(this, "Codice microchip duplicato!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}