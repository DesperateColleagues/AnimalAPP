package it.uniba.dib.sms22235.tasks.passionate;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalDiagnosisAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalExamsAdapter;
import it.uniba.dib.sms22235.adapters.animals.PokAnimalAdapter;
import it.uniba.dib.sms22235.adapters.purchases.ListViewPurchasesAdapter;
import it.uniba.dib.sms22235.entities.operations.PokeLink;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.DialogEntityDetailsFragment;
import it.uniba.dib.sms22235.tasks.common.views.userprofile.UserProfileInfoFragmentListener;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.DiagnosisFragment;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.AnimalProfile;

import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.ExamsFragment;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments.PhotoDiaryFragment;
import it.uniba.dib.sms22235.tasks.common.views.backbenches.BackbenchOperationsListener;
import it.uniba.dib.sms22235.tasks.common.views.reports.ReportAdditionListener;
import it.uniba.dib.sms22235.tasks.common.views.reports.ReportsListFragmentListener;
import it.uniba.dib.sms22235.tasks.common.views.requests.RequestsAnimalTransferOperationsListener;
import it.uniba.dib.sms22235.tasks.common.views.requests.RequestsStandardOperationListener;
import it.uniba.dib.sms22235.tasks.login.LoginActivity;
import it.uniba.dib.sms22235.tasks.passionate.fragments.PassionatePokAnimalList;
import it.uniba.dib.sms22235.tasks.passionate.fragments.PassionateProfileFragment;
import it.uniba.dib.sms22235.tasks.passionate.fragments.PassionateReservationFragment;

import it.uniba.dib.sms22235.tasks.passionate.fragments.PassionatePurchaseFragment;

import it.uniba.dib.sms22235.adapters.animals.AnimalPostAdapter;
import it.uniba.dib.sms22235.database.QueryPurchasesManager;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;
import it.uniba.dib.sms22235.utils.InterfacesOperationsHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This Activity manages the navigation of the passionate in the app. It is responsible for
 * db operations, and catches all the user actions by the callbacks from the interfaces implemented
 * */
public class PassionateNavigationActivity extends AppCompatActivity implements
        PassionateProfileFragment.ProfileFragmentListener,
        PassionatePurchaseFragment.PurchaseFragmentListener,
        PassionateReservationFragment.PassionateReservationFragmentListener,
        DiagnosisFragment.DiagnosisFragmentListener,
        AnimalProfile.AnimalProfileListener,
        AnimalProfile.AnimalProfileEditListener,
        ExamsFragment.ExamsFragmentListener,
        PhotoDiaryFragment.PhotoDiaryFragmentListener,
        PassionatePokAnimalList.PassionatePokAnimalListListener,
        NavigationActivityInterface,
        RequestsStandardOperationListener,
        RequestsAnimalTransferOperationsListener,
        UserProfileInfoFragmentListener,
        ReportsListFragmentListener,
        ReportAdditionListener,
        BackbenchOperationsListener,
        Serializable {

    private transient FirebaseFirestore db;
    private transient QueryPurchasesManager queryPurchases;

    private transient Passionate passionate;
    private transient LinkedHashSet<Animal> animalSet;
    private transient ArrayList<Purchase> purchasesList;
    private transient ArrayList<Reservation> passionateReservationsList;
    private transient ArrayList<Reservation> availableReservationsList;
    private transient ArrayList<Veterinarian> veterinariansList;
    private transient ArrayList<Organization> organizationList;
    private transient boolean online;

    private transient FloatingActionButton fab;
    private transient BottomNavigationView navView;
    private transient NavController navController;

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        boolean isProfile = navController.getCurrentDestination().getId() == R.id.passionate_profile;
        menu.findItem(R.id.profile_info).setVisible(isProfile);
        return true;
    }

    // Flag that specify whether the connection is enabled or not
    private boolean isConnectionEnabled;
    private MutableLiveData<Boolean> isConnectionEnabledMutable;

    // Callback that verifies the connectivity to a network at run time
    private final ConnectivityManager.NetworkCallback networkCallback =
            new ConnectivityManager.NetworkCallback() {

        @SuppressWarnings("unchecked")
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);

            isConnectionEnabled = true;
            isConnectionEnabledMutable.postValue(isConnectionEnabled);

            ArrayList<Purchase> purchasesOfflineList = (ArrayList<Purchase>)
                    DataManipulationHelper.readDataInternally(PassionateNavigationActivity.this,
                            KeysNamesUtils.FileDirsNames.ADD_PURCHASE);

            // Purchases offline operations
            if (purchasesOfflineList != null) {

                // Upload the offline files to fire store
                for (Purchase purchase : purchasesOfflineList) {
                    registerPurchaseFirebase(purchase);
                }

                // Delete the file once the process is over. If some network error occurs
                // during onPurchaseRegistered then a new offline file will be created
                // and will contain all the purchases that didn't make the upload to fire store
                deleteFile(KeysNamesUtils.FileDirsNames.ADD_PURCHASE);
            }
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);

            online = false;
            isConnectionEnabled = false;
            isConnectionEnabledMutable.postValue(isConnectionEnabled);

            Toast.makeText(getApplicationContext(), getString(R.string.offline_warning), Toast.LENGTH_SHORT).show();
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

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        setContentView(R.layout.activity_passionate_navigation);

        navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.passionate_profile,
                R.id.passionate_pet_care,
                R.id.passionate_purchase,
                R.id.passionate_requests)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_passionate_navigation);

        assert navHostFragment != null;

        // Set up the navigation system
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

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
            organizationList = (ArrayList<Organization>) loginBundle.getSerializable(KeysNamesUtils.BundleKeys.ORGANIZATIONS_LIST);
            online = loginBundle.getBoolean(KeysNamesUtils.BundleKeys.ONLINE);
        }

        isConnectionEnabledMutable = new MutableLiveData<>();
        isConnectionEnabledMutable.setValue(isConnectionEnabled);
        isConnectionEnabledMutable.observe(this, aBoolean -> {
            if (aBoolean && !online) {
                onlineDialog();
            } else if (!aBoolean && !online){
                navView.findViewById(R.id.passionate_pet_care).setOnClickListener(this::offlineSnackbar);
                navView.findViewById(R.id.passionate_requests).setOnClickListener(this::offlineSnackbar);
            }
        });

        // Init the animal data set if it is null
        if (animalSet == null) {
            animalSet = new LinkedHashSet<>();
        }

        // Init the purchases data set it is null
        if (purchasesList == null) {
            purchasesList = new ArrayList<>();
        }

        // Init the passionate reservations data set it is null
        if (passionateReservationsList == null) {
            passionateReservationsList = new ArrayList<>();
        }

        // Init the available reservations data set it is null
        if (availableReservationsList == null) {
            availableReservationsList = new ArrayList<>();
        }

        // Init the veterinarians data set it is null
        if (veterinariansList == null) {
            veterinariansList = new ArrayList<>();
        }

        // Init the organizations data set if it is null
        if (organizationList == null) {
            organizationList = new ArrayList<>();
        } else {
            Log.e("AnimalAPP - Passionate", "PassionateNavigationActivity:292 - Dimensione lista organizzazione: " + organizationList.size());
        }

        // Manage offline files
        String passionateOfflineFileName = KeysNamesUtils.FileDirsNames
                .currentPassionateOffline(passionate.getEmail());
        String localAnimalSetFileName = KeysNamesUtils.FileDirsNames
                .localAnimalsSet(passionate.getEmail());

        File filePassionateOffline = new File(passionateOfflineFileName);
        File fileLocalAnimalSet = new File(localAnimalSetFileName);

        if (!filePassionateOffline.exists()) {
            DataManipulationHelper.saveDataInternally(this, passionate, passionateOfflineFileName);
        }

        if (!fileLocalAnimalSet.exists() && animalSet.size() > 0) {
            DataManipulationHelper.saveDataInternally(this, animalSet, localAnimalSetFileName);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // standard behavior
        if (item.getItemId() == R.id.profile_info) {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(getString(R.string.username), passionate.getUsername());
            map.put(getString(R.string.numero_animali), "" + animalSet.size());
            map.put(getString(R.string.numero_spese), "" + purchasesList.size());

            Bundle bundle = new Bundle();
            bundle.putSerializable(KeysNamesUtils.BundleKeys.USER_PROFILE, passionate);
            bundle.putSerializable(KeysNamesUtils.BundleKeys.USER_PROFILE_INFO, map);

            navController.navigate(R.id.action_passionate_profile_to_userProfileInfoFragment, bundle);
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
            animal.setVeterinarian("");
            animal.setNature("");

            // Update the list of animals
            boolean isAdded = animalSet.add(animal);

            // Use the set to check the animal's uniqueness
            if (isAdded) {
                    registerAnimalFirebase(animal);

            } else {
                Toast.makeText(this, getString(R.string.animale_duplicato),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.salvataggio_animale_fallimento_rete),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPurchaseRegistered(@NonNull Purchase purchase) {
        purchase.setOwner(getUserId());

        purchasesList.add(purchase);

        long testValue = queryPurchases.insertPurchase(purchase.getId(),
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
            Toast.makeText(this, getString(R.string.error_spesa_inserimento), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onPurchaseDeleted(int pos, ArrayList<Purchase> dataSetPurchase, ListViewPurchasesAdapter adapter) {
        if (isConnectionEnabled()) {
            String id = dataSetPurchase.get(pos).getId();
            Purchase purchase = dataSetPurchase.get(pos);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            // Set dialog title
            @SuppressLint("InflateParams")
            View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_title);

            titleText.setText(getString(R.string.delete) + " " + dataSetPurchase.get(pos).getItemName());
            builder.setCustomTitle(titleView);
            // Show the purchase info
            builder.setMessage(R.string.delete_confirm_purchases);

            builder.setPositiveButton(R.string.elimina_def, ((dialog, which) ->{

            db.collection(KeysNamesUtils.CollectionsNames.PURCHASES)
                    .document(id).delete()
                    .addOnSuccessListener(unused -> {
                        // Delete purchase from all possibles data set
                        // First remove it from the total purchase list
                        purchasesList.remove(purchase);

                        if (!purchasesList.equals(dataSetPurchase))
                            dataSetPurchase.remove(pos);

                        queryPurchases.deleteLocalPurchaseById(id);
                        adapter.notifyDataSetChanged();
                    }); }));

            builder.setNegativeButton(R.string.annulla, ((dialog, which) -> dialog.dismiss()));

            builder.create().show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_offline), Toast.LENGTH_SHORT).show();
        }
    }

    // SECTION: POST MANAGEMENT

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void loadPost(AnimalPostAdapter adapter, List<PhotoDiaryPost> postsList, String animal) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper =
                new InterfacesOperationsHelper.AnimalCommonOperations(this, db);

        animalHelper.loadPost(adapter, postsList, animal);
    }

    @Override
    public void loadAnimalDiagnosis(AnimalDiagnosisAdapter adapter, RecyclerView recyclerView, String animal, AnimalDiagnosisAdapter.OnItemClickListener onClickListener){
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(this, db);
        animalHelper.getAnimalDiagnosis(adapter, recyclerView, animal, onClickListener);
    }

    @Override
    public void getAnimalExams(AnimalExamsAdapter adapter, RecyclerView recyclerView, String animal, AnimalExamsAdapter.OnItemClickListener onClickListener) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(this, db);
        animalHelper.getAnimalExams(adapter, recyclerView, animal, onClickListener);
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

        Log.e("AnimalAPP - Upload Foto", "PassionateNavigationActivity:459 - fileReference: " + fileReference);

        // Get a reference of the storage by passing the tree structure
        StorageReference storageReference = FirebaseStorage.getInstance().getReference
                (fileReference);

        // Give to the user a feedback to wait
        ProgressDialog progressDialog = new ProgressDialog(this,R.style.Widget_App_ProgressDialog);
        progressDialog.setMessage(getString(R.string.salvando_immagine));
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
                                        Toast.makeText(PassionateNavigationActivity.this,
                                                getString(R.string.caricamento_successo), Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    });
                        });
            }
        });
    }

    @Override
    public void onProfilePicAdded(Uri source, String microchip) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper =
                new InterfacesOperationsHelper.AnimalCommonOperations(this, db);

        animalHelper.onProfilePicAdded(source, microchip, getUserId());
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
                                    .delete().addOnSuccessListener(unused1 -> Toast.makeText(this, "Eliminazione completata con successo", Toast.LENGTH_SHORT).show());
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

    /* This method is used to load the animal profile pic in AnimalProfile */
    @Override
    public void loadProfilePic(String fileName, ImageView imageView) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(getApplicationContext(), db);
        animalHelper.loadProfilePic(fileName, imageView);
    }

    @Override
    public void onReservationBooked(@NonNull Reservation reservation) {
        String dateFormatted = reservation.getDate();
        String timeFormatted = reservation.getTime();

        availableReservationsList.remove(reservation);
        reservation.setOwner(getUserId());

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
                                        Toast.makeText(this, getString(R.string.prenotazione_conferma_successo), Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> {
                                        Log.d("AnimalAPP - Prenotazione visita","PassionateNavigationActivity:581 - " + e.getMessage());
                                    });
                        } else {
                            Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
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

    public List<Organization> getOrganizationList() {
        List<Organization> clonedOrganizationList = new ArrayList<>();

        for (Organization organization : organizationList) {
            clonedOrganizationList.add((Organization) organization.clone());
        }

        return clonedOrganizationList;
    }

    @Override
    public void onAnimalUpdated(@NonNull Animal animal) {
        if (isConnectionEnabled) {
            updateAnimalVeterinarian(animal);
        } else {
            Toast.makeText(this, getString(R.string.salvataggio_animale_fallimento_rete),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void checkIfAtHome(Animal animal, ImageView image) {
        InterfacesOperationsHelper.AnimalCommonOperations animalHelper = new InterfacesOperationsHelper.AnimalCommonOperations(this, db);
        animalHelper.checkIfAtHome(animal, image);
    }

    @Override
    public void onDialogChoosedVeterinarian(@NonNull Animal selectedAnimal) {
        onAnimalUpdated(selectedAnimal);
    }

    @Override
    public void loadOtherAnimal(Spinner spinner, String username, DialogFragment dialogFragment) {
        db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.OWNER, username)
                .get()
                .addOnSuccessListener(query -> {
                    List<DocumentSnapshot> documentSnapshots = query.getDocuments();

                    if (documentSnapshots.size() > 0) {
                        LinkedHashSet<Animal> otherAnimals = new LinkedHashSet<>();

                        for (DocumentSnapshot animal : documentSnapshots) {
                            otherAnimals.add(Animal.loadAnimal(animal));
                        }

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_dropdown_item, buildSpinnerEntries(otherAnimals));
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerAdapter);
                        spinner.setVisibility(View.VISIBLE);
                    } else {
                        String info = getString(R.string.impossibile_trovare_animali_di) + username;

                        DialogEntityDetailsFragment dialogEntityDetailsFragment = new DialogEntityDetailsFragment(info);
                        dialogEntityDetailsFragment.setPositiveButton(getString(R.string.chiudi), (dialog, which) -> {
                            dialog.dismiss();
                            dialogFragment.dismiss();
                        });
                        dialogEntityDetailsFragment.show(getSupportFragmentManager(), "DialogEntityDetailsFragment");
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void savePokeLink(String myCode, String otherCode, String type, String description, PokAnimalAdapter adapter) {
        // Task to take all the post of the user animal
        Task<QuerySnapshot> taskMyAnimalPic =
                db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                        .whereEqualTo(KeysNamesUtils.PhotoDiaryFields.FILE_NAME,
                                KeysNamesUtils.FileDirsNames.animalProfilePic(myCode))
                        .get();

        // Task to take all the post of the other animal
        Task<QuerySnapshot> taskOtherAnimalPic =
                db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                        .whereEqualTo(KeysNamesUtils.PhotoDiaryFields.FILE_NAME,
                                KeysNamesUtils.FileDirsNames.animalProfilePic(otherCode))
                        .get();

        // Synchronize tasks using whenAllComplete
        Tasks.whenAllComplete(taskMyAnimalPic, taskOtherAnimalPic)
                .addOnCompleteListener(task -> {
                    // Take the results of the task in the correct order
                    QuerySnapshot myAnimalPicSnapshot = (QuerySnapshot) task.getResult().get(0).getResult();
                    QuerySnapshot otherAnimalPicSnapshot = (QuerySnapshot) task.getResult().get(1).getResult();

                    PhotoDiaryPost myAnimalPic = null, otherAnimalPic = null;

                    // Load the post if is is found by checking the size of the snapshot
                    if (myAnimalPicSnapshot.getDocuments().size() > 0) {
                        myAnimalPic = PhotoDiaryPost.loadPhotoDiaryPost(myAnimalPicSnapshot.getDocuments().get(0));
                    }

                    if (otherAnimalPicSnapshot.getDocuments().size() > 0) {
                        otherAnimalPic = PhotoDiaryPost.loadPhotoDiaryPost(otherAnimalPicSnapshot.getDocuments().get(0));
                    }

                    String myAnimalUri = "", otherAnimalUri = "";

                    if (otherAnimalPic != null) {
                        otherAnimalUri = otherAnimalPic.getPostUri();
                    }

                    if (myAnimalPic != null) {
                        myAnimalUri = myAnimalPic.getPostUri();
                    }

                    // Instantiate the new poke link
                    PokeLink pokeLink = new PokeLink(
                            UUID.randomUUID().toString(),
                            myCode,
                            otherCode,
                            myAnimalUri,
                            otherAnimalUri,
                            type,
                            description,
                            getUserId()
                    );

                    // Update the adapter
                    adapter.addPokeLink(pokeLink);
                    adapter.notifyDataSetChanged();

                    // Update FireStore
                    db.collection(KeysNamesUtils.CollectionsNames.POKE_LINK)
                            .document(pokeLink.getId())
                            .set(pokeLink)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, getString(R.string.link_aggiunta_successo), Toast.LENGTH_SHORT).show();
                            });
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void deletePokeLink(String id, PokAnimalAdapter adapter) {
        db.collection(KeysNamesUtils.CollectionsNames.POKE_LINK)
                .document(id)
                .delete()
                .addOnSuccessListener(unused -> {
                    if (adapter.removeElementById(id)) {
                        adapter.notifyDataSetChanged();
                    }
                    Toast.makeText(this, getString(R.string.link_eliminazione_successo), Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void loadPokeLinks(PokAnimalAdapter adapter) {
        db.collection(KeysNamesUtils.CollectionsNames.POKE_LINK)
                .whereEqualTo(KeysNamesUtils.PokeLinkFields.PASSIONATE_EMAIL, getUserId())
                .get()
                .addOnSuccessListener(query -> {
                    List<DocumentSnapshot> documentSnapshots = query.getDocuments();
                    Log.e("AnimalAPP - PokAnimal", "PassionateNavigationActivity:817 - Numero di PokeLink: " + documentSnapshots.size());

                    if (documentSnapshots.size() > 0) {
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            adapter.addPokeLink(PokeLink.loadPokeLink(snapshot));
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @NonNull
    private ArrayList<String> buildSpinnerEntries(@NonNull LinkedHashSet<Animal> animals) {
        ArrayList<String> list = new ArrayList<>();

        for (Animal animal : animals) {
            list.add(animal.toString());
        }

        return list;
    }

    // NavigationActivityInterface overrides methods

    @Override
    public void restoreBottomAppBarVisibility(){
        if (navView.getVisibility() == View.GONE && fab.getVisibility() == View.GONE) {
            navView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Passionate getUser() {
        return passionate;
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
    public String getUserId() {
        return passionate.getUsername();
    }


    // Methods used to better manage certain operations


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
                                                getString(R.string.animale_inserimento_successo),
                                                Toast.LENGTH_LONG).show();

                                        // Update the local animal's files
                                        DataManipulationHelper.saveDataInternally(this, animalSet,
                                                KeysNamesUtils.FileDirsNames.localAnimalsSet(passionate.getEmail()));
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, getString(R.string.animale_duplicato),
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
    private void registerPurchaseFirebase(@NonNull Purchase purchase) {
        db.collection(KeysNamesUtils.CollectionsNames.PURCHASES)
                .document(purchase.getId())
                .set(purchase)
                .addOnSuccessListener(documentReference -> Toast.makeText(this,
                                getString(R.string.spesa_aggiunta_successo),
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
            if (date.equals(reservation.getDate())){
                clonedReservationsList.add((Reservation) reservation.clone());
            }
        }

        return clonedReservationsList;
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
                                    .set(animal)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this,
                                                getString(R.string.aggiornamento_completo),
                                                Toast.LENGTH_LONG).show();
                                        DataManipulationHelper.saveDataInternally(this, animalSet,
                                                KeysNamesUtils.FileDirsNames.localAnimalsSet(passionate.getEmail()));
                                        // Update the local animal's files
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, getString(R.string.error_generic),
                                                    Toast.LENGTH_SHORT).show());
                        }
                    }
                });
    }

    public boolean isConnectionEnabled() {
        return isConnectionEnabled;
    }

    private void offlineSnackbar(View view) {
        Snackbar snackbar = Snackbar.make(view, getResources().getString(R.string.error_offline),Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        TypedValue value = new TypedValue();
        this.getTheme().resolveAttribute(android.R.attr.windowBackground, value, true);
        snackbarView.setBackgroundColor(value.data);
        switch (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            case android.content.res.Configuration.UI_MODE_NIGHT_YES:
                textView.setTextColor(Color.WHITE);
                break;
            case android.content.res.Configuration.UI_MODE_NIGHT_NO:
                textView.setTextColor(Color.BLACK);
                break;
        }
        textView.setTextSize(15);
        snackbar.show();
    }

    private void onlineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);

        LayoutInflater inflater = this.getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_online, null);

        // Set dialog main options
        builder.setView(root);

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(getString(R.string.online_dialog_title));
        builder.setCustomTitle(titleView);

        final AlertDialog dialog = builder.create();

        root.findViewById(R.id.btnOnlineChoiceYes).setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
            this.finish();
        });
        root.findViewById(R.id.btnOnlineChoiceNo).setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }
}