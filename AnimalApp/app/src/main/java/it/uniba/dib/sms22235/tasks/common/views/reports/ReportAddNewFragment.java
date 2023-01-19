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
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.MloLink;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Backbench;
import it.uniba.dib.sms22235.entities.operations.Report;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.reports.DialogMap;
import it.uniba.dib.sms22235.tasks.common.dialogs.reports.DialogReportAddInfo;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class ReportAddNewFragment extends Fragment implements DialogMap.DialogMapListener, DialogReportAddInfo.DialogReportAddInfoListener {

    private ImageView imgReport;

    private TextView txtReportDetailTitle;
    private TextView txtReportDescription;
    private TextView txtPositionSumUp;

    private Button btnAddReportTitleAndDescription;
    private Button btnReportConfirm;
    private Button btnPosition;
    private Button btnAddReportImage;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private Report report;

    private String ownerEmail;

    private boolean isAdd = true;

    private LocationManager locationManager;
    private Location mLocation;
    private FusedLocationProviderClient client;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        assert container != null;

        db = ((NavigationActivityInterface) requireActivity()).getFireStoreInstance();
        storage = ((NavigationActivityInterface) requireActivity()).getStorageInstance();
        ownerEmail = Objects.requireNonNull(((NavigationActivityInterface) requireActivity())
                .getAuthInstance().getCurrentUser()).getEmail();

        // Create a first instance of the report
        report = new Report(UUID.randomUUID().toString(), ownerEmail);

        return inflater.inflate(R.layout.fragment_reports_add_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ReportDetailsAddAnimalFragment reportDetailsAddAnimalFragment = new ReportDetailsAddAnimalFragment();

        // Get the field to insert the report

        txtReportDetailTitle = view.findViewById(R.id.txtReportDetailTitle);
        txtReportDescription = view.findViewById(R.id.txtReportDescription);
        txtPositionSumUp = view.findViewById(R.id.txtPositionSumUp);

        btnAddReportTitleAndDescription = view.findViewById(R.id.btnAddReportTitleAndDescription);
        btnReportConfirm = view.findViewById(R.id.btnReportConfirm);
        btnPosition = view.findViewById(R.id.btnPosition);
        btnAddReportImage = view.findViewById(R.id.btnAddReportImage);

        imgReport = view.findViewById(R.id.imgReport);
        imgReport.setVisibility(View.GONE);

        // Call a dialog to add a title and a description to the report
        btnAddReportTitleAndDescription.setOnClickListener(v -> {
            DialogReportAddInfo dialogReportAddInfo = new DialogReportAddInfo();
            dialogReportAddInfo.setListener(this);
            dialogReportAddInfo.show(getChildFragmentManager(), "DialogReportAddInfo");
        });

        // Call a dialog to add the map position of the report
        btnPosition.setOnClickListener(v -> getCurrentLocation());

        btnAddReportImage.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            photoUploadAndSaveActivity.launch(i);
        });

        btnReportConfirm.setOnClickListener(v -> {
            if (requireActivity() instanceof PassionateNavigationActivity) {
                report.setReportAnimal(reportDetailsAddAnimalFragment.getSpinnerSelectedItem());
            } else {
                report.setReportAnimal("");
            }

            if (report.isReportReady()) {
                Toast.makeText(getContext(), "Report pronto per essere inserito", Toast.LENGTH_SHORT).show();

                // todo insert into firebase
            }
        });

        manageLayoutVisibilities();

        if (requireActivity() instanceof PassionateNavigationActivity) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, reportDetailsAddAnimalFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            view.findViewById(R.id.frame).setVisibility(View.GONE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation()
    {
        // Initialize Location manager
        locationManager = (LocationManager)requireActivity().getSystemService(Context.LOCATION_SERVICE);
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

    private void manageLayoutVisibilities() {
        if (!isAdd) {
            txtReportDescription.setVisibility(View.GONE);
            btnReportConfirm.setVisibility(View.GONE);
            btnAddReportTitleAndDescription.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPositionConfirmed(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext());

        try {
            // Get the address from the GeoCoder using the founded latitude and longitude
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 5);

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

    /**
     * This method is used to add the preview image of the report
     *
     * @param uri the uri of the image to add
     * @param email the email of the user whose adding the preview pic
     * */
    private void addReportPic(Uri uri, String email) {
        String fileName = KeysNamesUtils.FileDirsNames.reportPic(email);

        // Create the storage tree structure
        String fileReference = KeysNamesUtils.FileDirsNames.REPORT_POST +
                "/" + fileName;

        StorageReference storageReference = storage.getReference(fileReference);

        // Give to the user a feedback to wait
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Salvando l'immagine...");
        progressDialog.show();

        // Start the upload task
        UploadTask uploadTask = storageReference.putFile(uri);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().getStorage()
                        .getDownloadUrl().addOnCompleteListener(taskUri ->
                                // Set the uri for the report object
                                report.setReportHelpPictureUri(taskUri.getResult().toString()));
            }
        });
    }

}
