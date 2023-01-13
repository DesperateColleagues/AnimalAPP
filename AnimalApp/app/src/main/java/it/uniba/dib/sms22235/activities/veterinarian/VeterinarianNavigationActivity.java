package it.uniba.dib.sms22235.activities.veterinarian;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.ActivityInterface;
import it.uniba.dib.sms22235.activities.veterinarian.fragments.VeterinarianReservationFragment;
import it.uniba.dib.sms22235.entities.operations.Diagnosis;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class VeterinarianNavigationActivity extends AppCompatActivity implements
        VeterinarianReservationFragment.VeterinarianReservationFragmentListener,
        ActivityInterface {

    private FloatingActionButton fab;
    private FirebaseFirestore db;
    private Veterinarian veterinarian;
    private ArrayList<Reservation> reservationsList;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_veterinarian_navigation);

        BottomNavigationView navViewVeterinarian = findViewById(R.id.nav_view_vet);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.veterinarian_profile,
                R.id.veterinarian_animal_list,
                R.id.veterinarian_reservation,
                R.id.veterinarian_requests,
                R.id.veterinarian_backbench
        ).build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_veterinarian_navigation);

        assert navHostFragment != null;

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navViewVeterinarian, navController);

        fab = findViewById(R.id.floatingActionButton_veterinarian);

        db = FirebaseFirestore.getInstance();

        Bundle loginBundle = getIntent().getExtras();
        if (loginBundle != null){
            veterinarian = (Veterinarian) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.VETERINARIAN);
            reservationsList = (ArrayList<Reservation>) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.VETERINARIAN_RESERVATIONS);
        }
    }

    @Override
    public void onReservationRegistered(@NonNull Reservation reservation) {
        String dateFormatted = reservation.getDate();
        String timeFormatted = reservation.getTime();

        String docKeyReservation = KeysNamesUtils.CollectionsNames.RESERVATIONS
                +"_"+ dateFormatted.replaceAll("[-+^/]*", "")
        +"_"+ timeFormatted;

        reservationsList.add(reservation);

        db.collection(KeysNamesUtils.CollectionsNames.RESERVATIONS)
                .whereEqualTo(KeysNamesUtils.ReservationFields.DATE, reservation.getDate())
                .whereEqualTo(KeysNamesUtils.ReservationFields.TIME, reservation.getTime())
                .get()
                .addOnCompleteListener(task -> {
                 if (task.isSuccessful()){
                     QuerySnapshot querySnapshot = task.getResult();
                     if (querySnapshot.isEmpty()){
                         db.collection(KeysNamesUtils.CollectionsNames.RESERVATIONS)
                                 .document(docKeyReservation)
                                 .set(reservation)
                                 .addOnSuccessListener(unused -> {
                                     Toast.makeText(this, "Prenotazione inserita con successo!", Toast.LENGTH_SHORT).show();
                                 }).addOnFailureListener(e -> {
                                     Log.d("DEB", e.getMessage());
                                 });
                     } else {
                         Toast.makeText(this, "Appuntamento già presente.", Toast.LENGTH_SHORT).show();
                     }
                 }
                });
    }

    @Override
    public void onDiagnosisRegistered(Reservation reservation, Diagnosis diagnosis) {

        db.collection(KeysNamesUtils.CollectionsNames.DIAGNOSIS)
                .whereEqualTo(KeysNamesUtils.DiagnosisFields.ID, diagnosis.getId())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            db.collection(KeysNamesUtils.CollectionsNames.DIAGNOSIS)
                                    .document(diagnosis.getId())
                                    .set(diagnosis)
                                    .addOnSuccessListener(unused -> {
                                        updateReservation(reservation, diagnosis.getId());
                                        Toast.makeText(this,
                                                "Diagnosi inserita con successo",
                                                Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Errore interno, dati non aggiornati",
                                                    Toast.LENGTH_SHORT).show());
                        }
                    }
                });
    }

    private void updateReservation(Reservation reservation, String diagnosisID) {
        String dateFormatted = reservation.getDate();
        String timeFormatted = reservation.getTime();

        String docKeyReservation = KeysNamesUtils.CollectionsNames.RESERVATIONS
                +"_"+ dateFormatted.replaceAll("[-+^/]*", "")
                +"_"+ timeFormatted;


        db.collection(KeysNamesUtils.CollectionsNames.RESERVATIONS)
                .whereEqualTo(KeysNamesUtils.ReservationFields.DATE, reservation.getDate())
                .whereEqualTo(KeysNamesUtils.ReservationFields.VETERINARIAN, reservation.getVeterinarian())
                .whereEqualTo(KeysNamesUtils.ReservationFields.TIME, reservation.getTime())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            db.collection(KeysNamesUtils.CollectionsNames.RESERVATIONS)
                                    .document(docKeyReservation)
                                    .update(KeysNamesUtils.ReservationFields.DIAGNOSIS, diagnosisID)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this,
                                                "Prenotazione aggionrnata con successo!",
                                                Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Errore interno, dati non aggiornati",
                                                    Toast.LENGTH_SHORT).show());
                        }
                    }
                });
    }

    public ArrayList<Reservation> getDayReservationsList(String date) {
        ArrayList<Reservation> clonedReservationsList = new ArrayList<>();

        for (Reservation reservation : reservationsList) {
            if (date.equals(reservation.getDate())){
                clonedReservationsList.add((Reservation) reservation.clone());
            }
        }

        return clonedReservationsList;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    public String getUserId(){ return veterinarian.getEmail(); }

    @Override
    public FloatingActionButton getFab() {
        return fab;
    }

    @Override
    public FirebaseFirestore getFireStoreInstance() {
        return db;
    }

    @Override
    public FirebaseAuth getAuthInstance() {
        return FirebaseAuth.getInstance();
    }


    @Override
    public FirebaseStorage getStorageInstance() {
        return FirebaseStorage.getInstance();
    }

    public String getVeterinarianFullName(){ return veterinarian.getFullName(); }
}
