package it.uniba.dib.sms22235.tasks.veterinarian;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalListAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalDiagnosisAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalExamsAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalPostAdapter;
import it.uniba.dib.sms22235.entities.operations.Exam;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.AnimalProfile;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.DiagnosisFragment;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.ExamsFragment;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.PhotoDiaryFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.fragments.VeterinarianAnimalListFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.fragments.VeterinarianReservationFragment;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.utils.InterfacesOperationsHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class VeterinarianNavigationActivity extends AppCompatActivity implements
        VeterinarianReservationFragment.VeterinarianReservationFragmentListener,
        VeterinarianAnimalListFragment.VeterinarianAnimalListFragmentListener,
        AnimalProfile.AnimalProfileListener,
        ExamsFragment.ExamsFragmentListener,
        PhotoDiaryFragment.PhotoDiaryFragmentListener,
        DiagnosisFragment.DiagnosisFragmentListener,
        NavigationActivityInterface {

    private FloatingActionButton fab;
    private FirebaseFirestore db;
    private Veterinarian veterinarian;
    private ArrayList<Reservation> reservationsList;
    private transient  BottomNavigationView navViewVeterinarian;

    private InterfacesOperationsHelper helper;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_veterinarian_navigation);

        navViewVeterinarian = findViewById(R.id.nav_view_vet);

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

        helper = new InterfacesOperationsHelper(this);

        Bundle loginBundle = getIntent().getExtras();

        if (loginBundle != null){
            veterinarian = (Veterinarian) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.VETERINARIAN);
            reservationsList = (ArrayList<Reservation>) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.VETERINARIAN_RESERVATIONS);
        }

        if (reservationsList == null) {
            reservationsList = new ArrayList<>();
        }

        Toast.makeText(getApplicationContext(), "" + veterinarian.getClinicAddress(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onProfilePicAdded(Uri source, String microchip) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(this, db);
        animalHelper.onProfilePicAdded(source, microchip, getUserId());
    }

    @Override
    public void loadProfilePic(String fileName, ImageView imageView) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(getApplicationContext(), db);
        animalHelper.loadProfilePic(fileName, imageView);
    }

    public void getAssistedAnimals(AnimalListAdapter adapter, RecyclerView recyclerView) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.VETERINARIAN,
                        Objects.requireNonNull(auth.getCurrentUser()).getEmail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            List<DocumentSnapshot> assistedAnimalsDocuments = task.getResult().getDocuments();
                            for (DocumentSnapshot snapshot : assistedAnimalsDocuments) {
                                adapter.addAnimal(Animal.loadAnimal(snapshot));
                                Log.wtf("LISTA", Animal.loadAnimal(snapshot).toString());
                            }
                        }
                        recyclerView.setAdapter(adapter);
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void checkIfAtHome(Animal animal, ImageView image) {
        db.collection(KeysNamesUtils.CollectionsNames.RESIDENCE)
                .whereEqualTo("animal", animal.getMicrochipCode())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()){
                            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_house_siding_24));
                        } else {
                            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_home_24));
                        }
                    }
                });
    }

    @Override
    public void onPostDeleted(String uri, String microchip) {
        throw new UnsupportedOperationException();
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
    public FirebaseStorage getStorageInstance() {
        return FirebaseStorage.getInstance();
    }

    public String getVeterinarianFullName(){ return veterinarian.getFullName(); }

    @Override
    public FirebaseAuth getAuthInstance() {
        return FirebaseAuth.getInstance();
    }


    @Override
    public void restoreBottomAppBarVisibility(){
        if (navViewVeterinarian.getVisibility() == View.GONE && fab.getVisibility() == View.GONE) {
            navViewVeterinarian.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setNavViewVisibility(int visibility) {
        navViewVeterinarian.setVisibility(visibility);
    }

    @Override
    public void onPostShared(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getAnimalExams(AnimalExamsAdapter adapter, RecyclerView recyclerView, String animal){
        db.collection(KeysNamesUtils.CollectionsNames.EXAMS)
                .whereEqualTo(KeysNamesUtils.ExamsFields.EXAM_ANIMAL, animal)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()){
                            List<DocumentSnapshot> examsDocuments = task.getResult().getDocuments();
                            for (DocumentSnapshot snapshot : examsDocuments) {
                                adapter.addExam(Exam.loadExam(snapshot));
                                Log.wtf("Esami", Exam.loadExam(snapshot).toString());
                            }
                        }
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Nessun esame presente.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPostAdded(@NonNull PhotoDiaryPost post) {
        String fileName = System.currentTimeMillis() + "";

        post.setFileName(fileName);

        // Create the storage tree structure
        String fileReference = KeysNamesUtils.FileDirsNames.passionatePostDirName(getUserId()) +
                "/" +
                KeysNamesUtils.FileDirsNames.passionatePostRefDirAnimal(post.getPostAnimal())
                + "/" + fileName;

        // Get a reference of the storage by passing the tree structure
        StorageReference storageReference = FirebaseStorage.getInstance().getReference
                (fileReference);

        // Give to the user a feedback to wait
        ProgressDialog progressDialog = new ProgressDialog(this,R.style.Widget_App_ProgressDialog);
        progressDialog.setMessage("Salvando l'immagine...");
        progressDialog.show();

        // Start the upload task
        UploadTask uploadTask = storageReference.putFile(Uri.parse(post.getPostUri()));

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().getStorage()
                        .getDownloadUrl().addOnCompleteListener(taskUri -> {
                            // Set the download post URI
                            post.setPostUri(taskUri.getResult().toString());

                            // Save the post into the FireStore
                            db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                                    .document(fileName)
                                    .set(post)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(VeterinarianNavigationActivity.this,
                                                "Caricamento completato con successo", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    });
                        });
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void loadPost(AnimalPostAdapter adapter, List<PhotoDiaryPost> postsList, String animal) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper =
                new InterfacesOperationsHelper.AnimalCommonOperations(this, db);

        animalHelper.loadPost(adapter, postsList, animal);
    }

    @Override
    public void getAnimalDiagnosis(AnimalDiagnosisAdapter adapter, RecyclerView recyclerView, String animal, AnimalDiagnosisAdapter.OnItemClickListener onClickListener){
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(this, db);
        animalHelper.getAnimalDiagnosis(adapter, recyclerView, animal, onClickListener);
    }

    @Override
    public void onAnimalUpdated(Animal animal) {
        throw new UnsupportedOperationException();
    }
}
