package it.uniba.dib.sms22235.activities.passionate;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.fragments.DialogAddAnimalFragment;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.FirebaseNamesUtils;

public class PassionateNavigationActivity extends AppCompatActivity implements DialogAddAnimalFragment.DialogAddAnimalFragmentListener {

    private FirebaseFirestore db;
    private DialogAddAnimalFragment dialogAddAnimalFragment;

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

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        db = FirebaseFirestore.getInstance();

        fab.setOnClickListener(v -> {
            dialogAddAnimalFragment = new DialogAddAnimalFragment();
            dialogAddAnimalFragment.show(getSupportFragmentManager(), "DialogAddAnimalFragment");
        });
    }

    @Override
    public void onAnimalRegistered(@NonNull Animal animal) {
        String docKey = FirebaseNamesUtils.RolesNames.ANIMAL
                + "_" + animal.getMicrochipCode();

        db.collection(FirebaseNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(FirebaseNamesUtils.AnimalFields.MICROCHIP_CODE, animal.getMicrochipCode())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            db.collection(FirebaseNamesUtils.CollectionsNames.ANIMALS)
                                    .document(docKey)
                                    .set(animal)
                                    .addOnSuccessListener(unused -> dialogAddAnimalFragment.dismiss())
                                    .addOnFailureListener(e -> Log.d("DEB", e.getMessage()));
                        }
                    }
                });
    }
}