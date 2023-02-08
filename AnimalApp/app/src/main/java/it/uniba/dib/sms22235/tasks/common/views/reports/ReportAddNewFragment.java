package it.uniba.dib.sms22235.tasks.common.views.reports;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Report;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.DialogEntityDetailsFragment;
import it.uniba.dib.sms22235.tasks.common.dialogs.reports.DialogMap;
import it.uniba.dib.sms22235.tasks.common.dialogs.reports.DialogReportAddInfo;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class ReportAddNewFragment extends Fragment implements DialogMap.DialogMapListener,
        DialogReportAddInfo.DialogReportAddInfoListener {

    private ImageView imgReport;

    private TextView txtReportDetailTitle;
    private TextView txtReportDescription;
    private TextView txtPositionSumUp;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private Report report;

    private String ownerEmail;

    private boolean isAdd = true;

    private Location mLocation;
    private FusedLocationProviderClient client;

    private NavController controller;

    private boolean isGranted;

    private final ActivityResultLauncher<Intent> cropResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), resCrop -> {
                if (resCrop.getResultCode() == Activity.RESULT_OK) {
                    // Get intent data as result
                    Intent data = resCrop.getData();

                    if (data != null) {
                        // Get the output uri of the crop intent
                        Uri uri = UCrop.getOutput(data);

                        if (uri != null) {
                            imgReport.setVisibility(View.VISIBLE);
                            imgReport.setImageURI(uri);

                            // Update firestore and storage
                            report.setReportHelpPictureUri(uri.toString());
                        }
                    }
                }
            });

    private final ActivityResultLauncher<Intent> photoUploadAndSaveActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null && data.getData() != null) {
                        Uri sourceUri = data.getData();

                        // Destination URI of the cropped image
                        String destUriString = UUID.randomUUID().toString() + ".jpg";
                        Uri destUri = Uri.fromFile(new File(requireContext().getCacheDir(), destUriString));

                        // Crop the image to fit the correct aspect ratio
                        Intent cropIntent = UCrop.of(sourceUri, destUri)
                                .withAspectRatio(1, 1)
                                .getIntent(requireContext());

                        cropResult.launch(cropIntent);
                    }
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            this.isGranted = true;
        } else {
            Toast.makeText(getContext(), "Impossibile effettuare ricerche: abilitare " +
                            "il permesso alla posizione",
                    Toast.LENGTH_SHORT).show();
            this.isGranted = false;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = LocationServices.getFusedLocationProviderClient(requireActivity());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        assert container != null;

        controller = Navigation.findNavController(container);

        db = ((NavigationActivityInterface) requireActivity()).getFireStoreInstance();
        storage = ((NavigationActivityInterface) requireActivity()).getStorageInstance();
        ownerEmail = Objects.requireNonNull(((NavigationActivityInterface) requireActivity())
                .getAuthInstance().getCurrentUser()).getEmail();

        Bundle arguments = getArguments();

        if (arguments != null) {
            report = (Report) arguments.getSerializable(KeysNamesUtils.BundleKeys.REPORT_UPDATE);
            Toast.makeText(getContext(), "" + report.getReportDescription() , Toast.LENGTH_SHORT).show();
            isAdd = arguments.getBoolean(KeysNamesUtils.BundleKeys.REPORT_MODE_ADD);
        } else {
            // Create a first instance of the report
            report = new Report(UUID.randomUUID().toString(), ownerEmail);
        }

        return inflater.inflate(R.layout.fragment_reports_add_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the field to insert the report

        Spinner spinner = view.findViewById(R.id.spinnerReportAnimal);
        CheckBox checkBox = view.findViewById(R.id.checkBoxRep);

        if (requireActivity() instanceof PassionateNavigationActivity) {
            LinkedHashSet<Animal> animalSet = ((PassionateNavigationActivity) requireActivity()).getAnimalSet();

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_dropdown_item, buildSpinnerEntries(animalSet));
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
        } else {
            spinner.setVisibility(View.GONE);
            view.findViewById(R.id.txtReportDetailTitleAnimalSection).setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
            view.findViewById(R.id.divider).setVisibility(View.GONE);
        }

        checkBox.setOnClickListener(v -> {
            if (spinner.getVisibility() == View.VISIBLE) {
                spinner.setVisibility(View.GONE);
            } else {
                spinner.setVisibility(View.VISIBLE);
            }
        });

        txtReportDetailTitle = view.findViewById(R.id.txtReportDetailTitle);
        txtReportDescription = view.findViewById(R.id.txtReportDescription);
        txtPositionSumUp = view.findViewById(R.id.txtPositionSumUp);

        Button btnAddReportTitleAndDescription = view.findViewById(R.id.btnAddReportTitleAndDescription);
        Button btnReportConfirm = view.findViewById(R.id.btnReportConfirm);
        Button btnPosition = view.findViewById(R.id.btnPosition);
        Button btnAddReportImage = view.findViewById(R.id.btnAddReportImage);

        imgReport = view.findViewById(R.id.imgReport);
        imgReport.setVisibility(View.GONE);

        if (!isAdd) {
            loadInfoToUpdateReport();
        } else {
            report.setReportHelpPictureUri("");
        }

        // Call a dialog to add a title and a description to the report
        btnAddReportTitleAndDescription.setOnClickListener(v -> {
            DialogReportAddInfo dialogReportAddInfo = new DialogReportAddInfo();
            dialogReportAddInfo.setListener(this);
            dialogReportAddInfo.show(getChildFragmentManager(), "DialogReportAddInfo");
        });

        // Call a dialog to add the map position of the report
        btnPosition.setOnClickListener(v -> {
            String permissionAccessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
            String permissionAccessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;

            if (ActivityCompat.checkSelfPermission(getContext(), permissionAccessFineLocation) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), permissionAccessCoarseLocation) == PackageManager.PERMISSION_GRANTED || isGranted) {
                getCurrentLocation();
            } else  if (shouldShowRequestPermissionRationale(permissionAccessFineLocation)) {
                String info = "Il permesso <b>POSIZIONE</b> è essenziale per poter gestire tutte" +
                        " le informazioni relative alle segnalazione. Senza di esso non ti sarà possibile " +
                        "<br>• Inserire una nuova segnalazione" +
                        "<br>• Filtrare le segnalazioni esistenti";

                @SuppressLint("InflateParams")
                View titleView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialogs_title, null);
                TextView titleText = titleView.findViewById(R.id.dialog_title);
                titleText.setText("Perchè accettare il permesso");

                DialogEntityDetailsFragment dialogEntityDetailsFragment = new DialogEntityDetailsFragment(info);
                dialogEntityDetailsFragment.setTitleView(titleView);
                dialogEntityDetailsFragment.setPositiveButton("Chiudi", (dialog, which) -> dialog.dismiss());
                dialogEntityDetailsFragment.setNegativeButton("Chiedi permesso", ((dialog, which) -> {
                    requestPermissionLauncher.launch(permissionAccessFineLocation);
                    dialog.dismiss();
                }));

                dialogEntityDetailsFragment.show(getChildFragmentManager(), "DialogEntityDetailsFragment");
            }
            else {
                requestPermissionLauncher.launch(permissionAccessFineLocation);
            }
        });

        btnAddReportImage.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            photoUploadAndSaveActivity.launch(i);
        });

        // Confirm the report by checking fields and by adding it to the FireStore
        btnReportConfirm.setOnClickListener(v -> {
            if (requireActivity() instanceof PassionateNavigationActivity) {
                report.setReportAnimal((String) spinner.getSelectedItem());
            } else {
                report.setReportAnimal("");
            }

            // If the obligatory fields are filled then the report is ready to be submitted
            if (report.reportReady()) {
                Toast.makeText(getContext(), "Report pronto per essere inserito", Toast.LENGTH_SHORT).show();

                // If the picture uri is empty, just save the report to the FireStore
                // otherwise save the report's picture and the report reference to FireStore
                if (report.getReportHelpPictureUri().equals("")) {
                    db.collection(KeysNamesUtils.CollectionsNames.REPORTS)
                            .document(report.getReportId())
                            .set(report)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Segnalazione inserita con successo", Toast.LENGTH_SHORT).show();
                                controller.popBackStack();
                            });
                } else {
                    String fileName = report.getReportId();

                    // Create the storage tree structure
                    String fileReference = KeysNamesUtils.FileDirsNames.REPORT_POST +
                            "/" + KeysNamesUtils.FileDirsNames.reportPic(ownerEmail) + "/" + fileName;

                    StorageReference storageReference = storage.getReference(fileReference);

                    // Give to the user a feedback to wait
                    ProgressDialog progressDialog = new ProgressDialog(requireContext(),R.style.Widget_App_ProgressDialog);
                    progressDialog.setMessage("Salvando l'immagine...");
                    progressDialog.show();

                    // Start the upload task
                    UploadTask uploadTask = storageReference.putFile(Uri.parse(report.getReportHelpPictureUri()));

                    uploadTask.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            task.getResult().getStorage()
                                    .getDownloadUrl().addOnCompleteListener(taskUri -> {
                                        report.setReportHelpPictureUri(taskUri.getResult().toString());

                                        db.collection(KeysNamesUtils.CollectionsNames.REPORTS)
                                                .document(report.getReportId())
                                                .set(report)
                                                .addOnSuccessListener(unused -> {
                                                    Toast.makeText(getContext(), "Segnalazione inserita con successo", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    controller.popBackStack();
                                                });
                                    });
                        }
                    });
                }
            }
        });

        manageLayoutVisibilities();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled get last location

            client.getLastLocation().addOnCompleteListener(task -> {
                // Initialize location
                mLocation = task.getResult();

                // If the location is retrieved by the callback displays the DialogMap
                if (mLocation != null) {
                    DialogMap dialogMap = new DialogMap(new GeoPoint(mLocation));
                    dialogMap.setListener(this);
                    dialogMap.show(getChildFragmentManager(), "DialogMap");
                } else {
                    // Send a request to find the current location
                    LocationRequest locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10000)
                            .setFastestInterval(1000)
                            .setNumUpdates(1);

                    // Use a location callback to access the fetch results
                    LocationCallback locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            mLocation = locationResult.getLastLocation();

                            DialogMap dialogMap = new DialogMap(new GeoPoint(mLocation));
                            dialogMap.setListener(ReportAddNewFragment.this);
                            dialogMap.show(getChildFragmentManager(), "DialogMap");
                        }
                    };

                    // Request location updates
                    client.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper());
                }
            });
        } else {
            // When location service is not enabled open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public void onPositionConfirmed(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext());

        try {
            // Get the address from the GeoCoder using the founded latitude and longitude
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 5);

            txtPositionSumUp.setVisibility(View.VISIBLE);

            if (addresses.size() > 0) {
                txtPositionSumUp.setText(addresses.get(0).getAddressLine(0));
                report.setReportAddress(addresses.get(0).getAddressLine(0));
            } else {
                txtPositionSumUp.setText("Latitudine: " + latitude + "\nLongitudine: " + longitude);
                report.setReportAddress("");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        report.setLat(latitude);
        report.setLon(longitude);
    }

    @Override
    public void onInfoAdded(String title, String description) {
        txtReportDetailTitle.setText("Segnalazione - " + title);
        txtReportDescription.setVisibility(View.VISIBLE);
        txtReportDescription.setText(description);

        report.setReportTitle(title);
        report.setReportDescription(description);
    }


    private void loadInfoToUpdateReport() {
        txtReportDescription.setVisibility(View.VISIBLE);
        txtPositionSumUp.setVisibility(View.VISIBLE);

        txtReportDetailTitle.setText(report.getReportTitle());
        txtReportDescription.setText(report.getReportDescription());
        txtPositionSumUp.setText(report.getReportAddress());

        if (!report.getReportHelpPictureUri().equals("")) {
            imgReport.setVisibility(View.VISIBLE);
            Glide.with(requireContext()).load(Uri.parse(report.getReportHelpPictureUri())).into(imgReport);
        }

    }

    private void manageLayoutVisibilities() {
        if  (isAdd) {
            txtReportDescription.setVisibility(View.GONE);
        }
    }

    @NonNull
    private ArrayList<String> buildSpinnerEntries(@NonNull LinkedHashSet<Animal> animals) {
        ArrayList<String> list = new ArrayList<>();

        for (Animal animal : animals) {
            list.add(animal.toString());
        }

        return list;
    }

}
