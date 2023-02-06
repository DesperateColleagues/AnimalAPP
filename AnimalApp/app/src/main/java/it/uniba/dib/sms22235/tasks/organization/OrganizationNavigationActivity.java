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
import android.util.Log;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalListAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalDiagnosisAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalExamsAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalPostAdapter;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.userprofile.UserProfileInfoFragmentListener;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.AnimalProfile;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.DiagnosisFragment;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.ExamsFragment;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.PhotoDiaryFragment;
import it.uniba.dib.sms22235.tasks.common.views.requests.RequestsStandardOperationListener;
import it.uniba.dib.sms22235.tasks.login.LoginActivity;
import it.uniba.dib.sms22235.tasks.organization.fragments.OrganizationAnimalListFragment;
import it.uniba.dib.sms22235.tasks.organization.fragments.OrganizationImportDataFragment;
import it.uniba.dib.sms22235.utils.InterfacesOperationsHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class OrganizationNavigationActivity extends AppCompatActivity implements
        NavigationActivityInterface, DiagnosisFragment.DiagnosisFragmentListener,
        AnimalProfile.AnimalProfileListener,
        AnimalProfile.AnimalProfileEditListener,
        ExamsFragment.ExamsFragmentListener,
        PhotoDiaryFragment.PhotoDiaryFragmentListener,
        OrganizationImportDataFragment.OrganizationImportDataFragmentListener,
        RequestsStandardOperationListener,
        UserProfileInfoFragmentListener,
        OrganizationAnimalListFragment.OrganizationAnimalsFragmentListener {

    private transient Organization organization;

    private transient FirebaseFirestore db;
    private InterfacesOperationsHelper helper;

    private FloatingActionButton fab;
    private transient BottomNavigationView navView;

    private transient ArrayList<Veterinarian> veterinariansList;
    private transient NavController navController;

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

        navView = findViewById(R.id.nav_view_org);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.organization_profile,
                R.id.organization_animal_list,
                R.id.organization_import_data
        ).build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_organization_navigation);

        assert navHostFragment != null;

        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        helper = new InterfacesOperationsHelper(this);

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
            map.put("Indirizzo ente", organization.getOrgAddress());
            map.put("Numero di telefono", organization.getPhoneNumber());
            map.put("Tipo di ente", organization.getPurpose());

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
    @Override
    public String getUserId() {
        return organization.getOrgName();
    }

    @Override
    public void restoreBottomAppBarVisibility(){
        if (navView.getVisibility() == View.GONE && fab.getVisibility() == View.GONE) {
            navView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getAssistedAnimals(AnimalListAdapter adapter, RecyclerView recyclerView) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        Task<QuerySnapshot> taskOrgAnimals =
                db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                        .whereEqualTo(KeysNamesUtils.AnimalFields.OWNER,
                                Objects.requireNonNull(auth.getCurrentUser()).getEmail())
                        .get();

        /*Task<QuerySnapshot> taskBackbenchAnimals =
                db.collection(KeysNamesUtils.CollectionsNames.RESIDENCE)
                        .whereEqualTo(KeysNamesUtils.AnimalFields.VETERINARIAN,
                                Objects.requireNonNull(auth.getCurrentUser()).getEmail())
                        .get();*/

        Tasks.whenAllComplete(taskOrgAnimals/*, taskBackbenchAnimals*/)
                .addOnCompleteListener(task -> {
                    QuerySnapshot organizationAnimalsSnapshot = (QuerySnapshot) task.getResult().get(0).getResult();
                    if (!organizationAnimalsSnapshot.isEmpty()) {
                        List<DocumentSnapshot> assistedAnimalsDocuments = organizationAnimalsSnapshot.getDocuments();
                        for (DocumentSnapshot snapshot : assistedAnimalsDocuments) {
                            adapter.addAnimal(Animal.loadAnimal(snapshot));
                            Log.wtf("LISTA", Animal.loadAnimal(snapshot).toString());
                        }
                    }
                    recyclerView.setAdapter(adapter);
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
            Toast.makeText(this, "Impossibile modificare l'animale: rete assente",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getAnimalDiagnosis(AnimalDiagnosisAdapter adapter, RecyclerView recyclerView, String animal, AnimalDiagnosisAdapter.OnItemClickListener onClickListener) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(this, db);
        animalHelper.getAnimalDiagnosis(adapter, recyclerView, animal, onClickListener);
    }

    @Override
    public void getAnimalExams(AnimalExamsAdapter adapter, RecyclerView recyclerView, String animal) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(this, db);
        animalHelper.getAnimalExams(adapter, recyclerView, animal, getSupportFragmentManager());
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
                                        Toast.makeText(this, "Eliminazione completata con successo", Toast.LENGTH_SHORT).show();
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
