package it.uniba.dib.sms22235.tasks.organization;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalListAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalDiagnosisAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalExamsAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalPostAdapter;
import it.uniba.dib.sms22235.entities.operations.AnimalResidence;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.ImportedAnimal;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.views.userprofile.UserProfileInfoFragmentListener;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.AnimalProfile;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.DiagnosisFragment;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.ExamsFragment;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.PhotoDiaryFragment;
import it.uniba.dib.sms22235.tasks.common.views.backbenches.BackbenchOperationsListener;
import it.uniba.dib.sms22235.tasks.common.views.reports.ReportAdditionListener;
import it.uniba.dib.sms22235.tasks.common.views.reports.ReportsListFragmentListener;
import it.uniba.dib.sms22235.tasks.common.views.requests.RequestsStandardOperationListener;
import it.uniba.dib.sms22235.tasks.login.LoginActivity;
import it.uniba.dib.sms22235.tasks.organization.fragments.OrganizationAnimalListFragment;
import it.uniba.dib.sms22235.tasks.organization.fragments.OrganizationImportDataFragment;
import it.uniba.dib.sms22235.tasks.organization.fragments.OrganizationProfileFragment;
import it.uniba.dib.sms22235.utils.InterfacesOperationsHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This Activity manages the navigation of the organization in the app. It is responsible for
 * db operations, and catches all the user actions by the callbacks from the interfaces implemented
 * */
public class OrganizationNavigationActivity extends AppCompatActivity implements
        NavigationActivityInterface, DiagnosisFragment.DiagnosisFragmentListener,
        AnimalProfile.AnimalProfileListener,
        AnimalProfile.AnimalProfileEditListener,
        ExamsFragment.ExamsFragmentListener,
        PhotoDiaryFragment.PhotoDiaryFragmentListener,
        OrganizationImportDataFragment.OrganizationImportDataFragmentListener,
        RequestsStandardOperationListener,
        UserProfileInfoFragmentListener,
        OrganizationAnimalListFragment.OrganizationAnimalsFragmentListener,
        OrganizationProfileFragment.OrganizationProfileFragmentListener,
        ReportsListFragmentListener,
        BackbenchOperationsListener,
        ReportAdditionListener {

    private transient Organization organization;

    private transient FirebaseFirestore db;

    private FloatingActionButton fab;
    private transient BottomNavigationView navView;

    private transient ArrayList<Veterinarian> veterinariansList;
    private transient NavController navController;

    // Flag that specify whether the connection is enabled or not
    private boolean isConnectionEnabled;

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        boolean isProfile = navController.getCurrentDestination().getId() == R.id.organization_profile;
        menu.findItem(R.id.profile_info).setVisible(isProfile);
        return true;
    }

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

                    Toast.makeText(getApplicationContext(), getString(R.string.offline_warning), Toast.LENGTH_SHORT).show();
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
        navView = findViewById(R.id.nav_view_org);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.organization_profile,
                R.id.organization_import_data,
                R.id.organization_requests,
                R.id.backBenchFragment
        ).build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_organization_navigation);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        Bundle loginBundle = getIntent().getExtras(); // get the login bundle
        // Extract the bundle data sent from login activity
        if (loginBundle != null) {
            organization = (Organization) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.ORGANIZATION);
            veterinariansList = (ArrayList<Veterinarian>) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.VETERINARIANS_LIST);
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


        // Init the veterinatians data set it is null
        if (veterinariansList == null) {
            veterinariansList = new ArrayList<>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // standard behavior
        if (item.getItemId() == R.id.profile_info) {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(getString(R.string.indirizzo_ente), organization.getOrgAddress());
            map.put(getString(R.string.numero_telefono), organization.getPhoneNumber());
            map.put(getString(R.string.tipo_ente), organization.getPurpose());

            Bundle bundle = new Bundle();
            bundle.putSerializable(KeysNamesUtils.BundleKeys.USER_PROFILE, organization);
            bundle.putSerializable(KeysNamesUtils.BundleKeys.USER_PROFILE_INFO, map);

            navController.navigate(R.id.action_organization_profile_to_userProfileInfoFragment, bundle);
        } else if (item.getItemId() == R.id.logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent, null);
            finish();
        } else {
            navController.popBackStack();
            restoreBottomAppBarVisibility();
        }

        return true;
    }

    @Override
    public void setNavViewVisibility(int visibility) {
        navView.setVisibility(visibility);
    }

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

    @Override
    public FirebaseAuth getAuthInstance() {
        return FirebaseAuth.getInstance();
    }


    @Override
    public void onAnimalRegistered(@NonNull Animal animal) {
        if (isConnectionEnabled) {
            registerAnimalFirebase(animal);
        }
    }

    @Override
    public void uploadPhotos(@NonNull ImportedAnimal animal){
        getStorageInstance().getReference(animal.getProfilePicPath()).getBytes(1024 * 1024 * 5).addOnSuccessListener(bytes -> {
            String profilePicName = animal.getProfilePicPath().split("/")[1].split(".jpg")[0];
            getStorageInstance()
                    .getReference(getUserId() + "_post/" + profilePicName)
                    .putBytes(bytes)
                    .addOnCompleteListener(task -> {
                        task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                            PhotoDiaryPost post = new PhotoDiaryPost(uri.toString(), animal.getMicrochipCode());
                            post.setFileName(profilePicName);

                            // Create the photo diary profile picture
                            db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                                    .document(profilePicName)
                                    .set(post);


                            String title = String.format(getString(R.string.title_offerta_animale_import), animal.getAnimalSpecies(), animal.getRace());
                            String body = String.format(getString(R.string.offerta_animale_import), animal.getAnimalSpecies(), animal.getName());

                            Request request = new Request(title, body, KeysNamesUtils.RequestFields.R_TYPE_ANIMAL_OFFER);
                            request.setUserEmail(organization.getEmail());
                            request.setAnimal(animal.getName() + " - " + animal.getMicrochipCode());

                            SimpleDateFormat dateSDF = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
                            String currentDate = dateSDF.format(new Date());
                            AnimalResidence animalResidence = new AnimalResidence(currentDate, "9999-12-31",organization.getEmail(),"true");
                            animalResidence.setAnimal(animal.getMicrochipCode());
                            db.collection(KeysNamesUtils.CollectionsNames.REQUESTS)
                                    .document(request.getId())
                                    .set(request);
                            db.collection(KeysNamesUtils.CollectionsNames.RESIDENCE)
                                    .document(animal.getMicrochipCode() + "_" + organization.getEmail())
                                    .set(animalResidence);
                        });
                    });
        });
    }

    /**
     * This method is used to register an animal into the Firebase firestore
     *
     * @param animal the animal to be registered
     * */
    private void registerAnimalFirebase(@NonNull Animal animal) {
        String docKeyAnimal = KeysNamesUtils.RolesNames.ANIMAL
                + "_" + animal.getMicrochipCode();

        animal.setOwner(getUserId());

        // Check if the microchip code is duplicated first
        db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.MICROCHIP_CODE, animal.getMicrochipCode())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If no animal own that microchip code, then it is possible to insert
                        if (task.getResult().isEmpty()) {
                            db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                                    .document(docKeyAnimal)
                                    .set(animal);
                        }
                    }
                });
    }
    @Override
    public String getUserId() {
        return organization.getEmail();
    }

    @Override
    public void restoreBottomAppBarVisibility(){
        if (navView.getVisibility() == View.GONE && fab.getVisibility() == View.GONE) {
            navView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Organization getUser() {
        return organization;
    }

    @Override
    public void getAssistedAnimals(AnimalListAdapter adapter, RecyclerView recyclerView) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(this, db);

        Task<QuerySnapshot> passionateAnimals = db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.VETERINARIAN,
                        Objects.requireNonNull(auth.getCurrentUser()).getEmail()).get();

        Task<QuerySnapshot> backbenchAnimals = db.collection(KeysNamesUtils.CollectionsNames.RESIDENCE)
                .whereEqualTo(KeysNamesUtils.ResidenceFields.RESIDENCE_OWNER,
                        Objects.requireNonNull(auth.getCurrentUser()).getEmail()).get();

        Tasks.whenAllComplete(passionateAnimals,backbenchAnimals)
                .addOnCompleteListener(task -> {
                    ArrayList<Animal> animals = new ArrayList<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot passionateSnapshot = (QuerySnapshot) task.getResult().get(0).getResult();
                        QuerySnapshot backbenchSnapshot = (QuerySnapshot) task.getResult().get(1).getResult();

                        if (!passionateSnapshot.isEmpty()) {
                            List<DocumentSnapshot> passionateSnapshotDocuments = passionateSnapshot.getDocuments();
                            for (DocumentSnapshot snapshot : passionateSnapshotDocuments) {
                                animals.add(Animal.loadAnimal(snapshot));
                            }
                        }
                        if (!backbenchSnapshot.isEmpty()) {
                            List<DocumentSnapshot> assistedAnimalsMicrochipDocuments = backbenchSnapshot.getDocuments();
                            ArrayList<String> assistedAnimalsMicrochip = new ArrayList<>();
                            for (DocumentSnapshot snapshot : assistedAnimalsMicrochipDocuments) {
                                AnimalResidence ar = AnimalResidence.loadResidence(snapshot);
                                if (animalHelper.checkDate(ar.getStartDate(), ar.getEndDate())) {
                                    assistedAnimalsMicrochip.add(ar.getAnimal());
                                }
                            }
                            if (!assistedAnimalsMicrochip.isEmpty()) {
                                db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                                        .whereIn(KeysNamesUtils.AnimalFields.MICROCHIP_CODE,
                                                assistedAnimalsMicrochip)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                QuerySnapshot assistedAnimalsSnapshot = task1.getResult();
                                                List<DocumentSnapshot> assistedAnimalsDocuments = assistedAnimalsSnapshot.getDocuments();
                                                for (DocumentSnapshot snapshot : assistedAnimalsDocuments) {
                                                    Animal a = Animal.loadAnimal(snapshot);
                                                    if (!animals.contains(a)) {
                                                        animals.add(a);
                                                    }
                                                }
                                            }
                                            adapter.addAllAnimals(animals);
                                            recyclerView.setAdapter(adapter);
                                        });
                            }
                        } else {
                            adapter.addAllAnimals(animals);
                            recyclerView.setAdapter(adapter);
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

    @Override
    public void onAnimalUpdated(Animal animal) {
        InterfacesOperationsHelper.AnimalOwnerOperations animalHelper = new InterfacesOperationsHelper.AnimalOwnerOperations(this, db);
        animalHelper.updateAnimal(animal);
    }

    @Override
    public void checkIfAtHome(Animal animal, ImageView image) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(this, db);
        animalHelper.checkIfAtHome(animal, image);
    }

    @Override
    public List<Veterinarian> getVeterinarianList() {
        InterfacesOperationsHelper.AnimalOwnerOperations animalHelper = new InterfacesOperationsHelper.AnimalOwnerOperations(this, db);
        return animalHelper.getVeterinariansList(veterinariansList);
    }

    @Override
    public void onDialogChoosedVeterinarian(@NonNull Animal selectedAnimal) {
        if (isConnectionEnabled) {
            InterfacesOperationsHelper.AnimalOwnerOperations animalHelper = new InterfacesOperationsHelper.AnimalOwnerOperations(this, db);
            animalHelper.onAnimalUpdated(selectedAnimal);
        } else {
            Toast.makeText(this, getString(R.string.modifica_animale_fallita_rete),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void loadAnimalDiagnosis(AnimalDiagnosisAdapter adapter, RecyclerView recyclerView, String animal, AnimalDiagnosisAdapter.OnItemClickListener onClickListener) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(this, db);
        animalHelper.getAnimalDiagnosis(adapter, recyclerView, animal, onClickListener);
    }

    @Override
    public void getAnimalExams(AnimalExamsAdapter adapter, RecyclerView recyclerView, String animal, AnimalExamsAdapter.OnItemClickListener onClickListener) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(this, db);
        animalHelper.getAnimalExams(adapter, recyclerView, animal, onClickListener);
    }

    @Override
    public void onPostAdded(PhotoDiaryPost post) {
        InterfacesOperationsHelper.AnimalOwnerOperations animalHelper = new InterfacesOperationsHelper.AnimalOwnerOperations(this, db);
        animalHelper.onPostAdded(post, getUserId());
    }

    @Override
    public void loadPost(AnimalPostAdapter adapter, List<PhotoDiaryPost> postsList, String animal) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper =
                new InterfacesOperationsHelper.AnimalCommonOperations(this, db);

        animalHelper.loadPost(adapter, postsList, animal);
    }

    @Override
    public void onPostDeleted(String url, String microchip) {
        StorageReference storageReference = getStorageInstance().getReferenceFromUrl(url);

        storageReference.delete().addOnSuccessListener(unused -> {
            db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                    .whereEqualTo(KeysNamesUtils.PhotoDiaryFields.POST_URI, url)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.size() > 0) {
                            db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                                    .document(PhotoDiaryPost.loadPhotoDiaryPost(queryDocumentSnapshots.getDocuments().get(0)).getFileName())
                                    .delete().addOnSuccessListener(unused1 -> {
                                        Toast.makeText(this, getString(R.string.cancellazione_successo), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
        });
    }

    @Override
    public void onPostShared(String url) {
        Glide.with(this).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                        resource, System.currentTimeMillis() + "", null);
                Uri imageUri =  Uri.parse(path);

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(i);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // not implemented
            }
        });
    }
}
